package app.worker;

import java.awt.Point;

import star.annotations.Handles;
import star.annotations.Properties;
import star.annotations.Property;
import star.annotations.SignalComponent;
import star.hydrology.data.interfaces.Grid;
import star.hydrology.data.layers.FloatDataset;
import star.hydrology.data.layers.ProjectedTerrainMap;
import star.hydrology.events.GridStatisticsProviderChangeRaiser;
import star.hydrology.events.IUHEvent;
import star.hydrology.events.IUHRaiser;
import star.hydrology.events.RainfallGaugesRaiser;
import star.hydrology.events.SimpleIUHAdjustPanelRaiser;
import star.hydrology.events.giuh.GIUHVelocitiesRaiser;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.events.interfaces.PaletteRenderableLayer;
import star.hydrology.events.map.FlowaccumulationMapLayerRaiser;
import star.hydrology.events.map.FlowdirectionMapLayerRaiser;
import star.hydrology.events.map.SimpleIUHMapLayerEvent;
import star.hydrology.events.map.SimpleIUHMapLayerRaiser;
import star.hydrology.events.map.WatershedLayerRaiser;
import utils.ArrayNumerics;

@SignalComponent()
@Properties( { @Property(name = "flowAcc", type = PaletteRenderableLayer.class) //
        , @Property(name = "flowDir", type = PaletteRenderableLayer.class) //
        , @Property(name = "watershed", type = PaletteRenderableLayer.class) //
        , @Property(name = "layer", type = PaletteRenderableLayer.class, getter = Property.PUBLIC) //
        , @Property(name = "watershedOrigin", type = Point.class) //
        , @Property(name = "threshold", type = float.class) //
})
public class SimpleIUH extends SimpleIUH_generated implements star.hydrology.events.interfaces.HeightsRange
{
	private FloatDataset iuh;
	private PaletteRenderableLayer raingauges;
	private float[][] raingaugesIUH;
	private float ratio = 10;

	private float STREAM_INCREMENT = .1f;
	private float HILLSLOPE_INCREMENT = .5f;

	private float stream_velocity = 0;
	private float hillslope_velocity = 0;
	private int raingaugesCount;

	private void setRatio(float current)
	{
		ratio = current;
		HILLSLOPE_INCREMENT = STREAM_INCREMENT * ratio;
		calculate();
	}

	@Handles(raises = {})
	void updateRaingaugeLayer(RainfallGaugesRaiser raiser)
	{
		this.raingauges = raiser.getLayer();
		raingaugesCount = raiser.getGauges().size();
		calculate();
	}

	public int getNumberOfGauges()
	{
		return raingaugesCount;
	}

	public float getValueAt(int gauge, float value)
	{
		if (value > getMinimum() && value < getMaximum())
		{
			final int points = discharge.length;
			float step = (getMaximum() - getMinimum()) / (points - 1);
			int index = (int) ((value - getMinimum()) / step);
			return raingaugesIUH[gauge][index];
		}
		else
		{
			return 0;
		}
	}

	private void calculate()
	{
		// this goes from origin and add time lag to each cell...
		// that should produce simple iuh
		Point watershedOrigin = getWatershedOrigin();
		if (watershedOrigin != null && getFlowDir() != null && getFlowAcc() != null)
		{
			HILLSLOPE_INCREMENT = getFlowDir().getDataset().getCellsize() / hillslope_velocity;
			STREAM_INCREMENT = getFlowDir().getDataset().getCellsize() / stream_velocity;
			try
			{
				iuh = (FloatDataset) ((FloatDataset) getFlowAcc().getDataset()).getSameCoverage(FloatDataset.class);
			}
			catch (InstantiationException e)
			{
				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
			size = 0;
			cols = iuh.getCols();
			rows = iuh.getRows();
			walk(watershedOrigin.x, watershedOrigin.y, 0, 1);
			ProjectedTerrainMap m = new ProjectedTerrainMap();
			m.setXYZDataset(iuh);
			m.setLayerName("Instantaneous Unit Hydrograph");
			setLayer(m);
			calculateIUHStatistics();
			(new SimpleIUHMapLayerEvent(this)).raise();

		}
	}

	private final int[] offsetX = new int[] { 1, 1, 0, -1, -1, -1, 0, 1 };
	private final int[] offsetY = new int[] { 0, 1, 1, 1, 0, -1, -1, -1 };
	private final int[] offsetDir = new int[] { 16, 32, 64, 128, 1, 2, 4, 8 };
	private int size;
	private int cols;
	private int rows;

	private final static float sqrt2 = (float) Math.sqrt(2);

	private void walk(int x, int y, float time, float distance)
	{
		size++;
		iuh.setElementAt(x, y, time);
		time += timeIncrement(x, y, distance);
		for (int i = 0; i < offsetX.length; i++)
		{
			if (offsetDir[i] == (int) getFlowDir().getDataset().getElementAt(x + offsetX[i], y + offsetY[i]))
			{
				int d = (offsetX[i] != 0 ? 1 : 0) + (offsetY[i] != 0 ? 1 : 0);
				int xx = x + offsetX[i];
				int yy = y + offsetY[i];
				if (xx < 0 || xx >= cols || yy < 0 || y >= rows)
				{
					return;
				}
				walk(xx, yy, time, d == 1 ? 1f : sqrt2);
			}
		}
	}

	private float timeIncrement(int x, int y, float distance)
	{
		float ret;
		if (getFlowAcc().getDataset().getElementAt(x, y) > getThreshold())
		{
			ret = STREAM_INCREMENT;
		}
		else
		{
			ret = HILLSLOPE_INCREMENT;
		}
		ret *= distance;
		return ret;
	}

	private int numberOfBins = 200;

	public int getNumberOfBins()
	{
		return numberOfBins;
	}

	public void setNumberOfBins(int nr)
	{
		numberOfBins = nr;
	}

	private void calculateIUHStatistics()
	{
		if (getLayer() != null)
		{
			final Grid dataset = getLayer().getDataset();
			final int cols = dataset.getCols();
			final int rows = dataset.getRows();
			float min = Float.MAX_VALUE;
			float max = Float.MIN_VALUE;
			for (int y = 0; y < rows; y++)
			{
				for (int x = 0; x < cols; x++)
				{
					float v = dataset.getElementAt(x, y);
					if (!Float.isNaN(v))
					{
						max = Math.max(v, max);
						min = Math.min(v, min);
					}
				}
			}
			float step;
			if (max - min < 24)
			{
				step = .25f;
			}
			else if (max - min < 96)
			{
				step = .5f;
			}
			else if (max - min > 200)
			{
				step = (int) ((max - min) / 200);
			}
			else
			{
				step = 1f;
			}
			int points = (max - min) == 0 ? 10 : (int) ((max - min) / step) + 1;
			max = min + points * step;
			setNumberOfBins(points);
			float count[] = new float[points + 1];

			float[][] raingaugeIUHs = new float[raingaugesCount + 1][points + 1];

			java.util.Arrays.fill(count, 0);
			for (int y = 0; y < rows; y++)
			{
				for (int x = 0; x < cols; x++)
				{
					float v = dataset.getElementAt(x, y);
					if (!Float.isNaN(v))
					{
						int index = (int) ((v - min) / step);
						count[index]++;
						if (raingauges != null)
						{
							float value = raingauges.getDataset().getElementAt(x, y);
							if (!Float.isNaN(value))
							{
								int i = (int) value;
								if (i >= 0 && i <= raingaugesCount)
								{
									raingaugeIUHs[i][index]++;
								}
							}
						}
					}
				}
			}
			ArrayNumerics.normalize(count);
			ArrayNumerics.normalize(raingaugeIUHs);
			this.max = max;
			this.min = min;
			this.discharge = count;
			this.raingaugesIUH = raingaugeIUHs;
		}
		else
		{
			this.max = 0;
			this.min = 0;
			this.discharge = new float[] { 0 };
			this.raingaugesIUH = new float[][] { { 0 } };
		}
		(new IUHEvent(this)).raise();
	}

	private float max, min;
	private float[] discharge;

	public float getMaximum()
	{
		return max;
	}

	public float getMinimum()
	{
		return min;
	}

	public float getValueAt(float value)
	{
		if (value > getMinimum() && value < getMaximum())
		{
			final int points = discharge.length;
			float step = (getMaximum() - getMinimum()) / (points - 1);
			int index = (int) ((value - getMinimum()) / step);
			return discharge[index];
		}
		else
		{
			return 0;
		}
	}

	public int getKind()
	{
		return LayerConstants.SIMPLEIUHLAYER;
	}

	public int getLayerKind()
	{
		return getKind();
	}

	@Handles(raises = { IUHRaiser.class, SimpleIUHMapLayerRaiser.class })
	void handleEvent(SimpleIUHAdjustPanelRaiser r)
	{
		if (r.getKind() == LayerConstants.SIMPLEIUHLAYER)
		{
			setRatio(r.getCurrent());
		}
	}

	@Handles(raises = { IUHRaiser.class, SimpleIUHMapLayerRaiser.class })
	void handle(FlowaccumulationMapLayerRaiser fa, FlowdirectionMapLayerRaiser fd, WatershedLayerRaiser ws)
	{
		setFlowAcc(fa.getLayer());
		setFlowDir(fd.getLayer());
		setWatershed(ws.getLayer());
		setWatershedOrigin(ws.getWatershedOrigin());
		calculate();
	}

	@Handles(raises = {})
	void handleRatios(GIUHVelocitiesRaiser r)
	{
		ratio = r.getChannelVelocity() / r.getHillslopeVelocity();
		hillslope_velocity = r.getHillslopeVelocity();
		stream_velocity = r.getChannelVelocity();
		calculate();
	}

	@Handles(raises = {})
	void setAccumulationTreshold(GridStatisticsProviderChangeRaiser r)
	{
		if (r.getKind() == LayerConstants.STREAMS)
		{
			setThreshold(r.getCurrent());
			calculate();
			System.out.println("SimpleIUH Threshold  " + r.getCurrent());
		}
	}
}
