package app.worker.pdf;

import java.awt.Point;

import star.annotations.Handles;
import star.annotations.Properties;
import star.annotations.Property;
import star.annotations.SignalComponent;
import star.hydrology.data.interfaces.Grid;
import star.hydrology.data.layers.FloatDataset;
import star.hydrology.data.layers.ProjectedTerrainMap;
import star.hydrology.events.SlopePDFEvent;
import star.hydrology.events.SlopePDFRaiser;
import star.hydrology.events.interfaces.HeightsRange;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.events.interfaces.PaletteRenderableLayer;
import star.hydrology.events.map.FlowaccumulationMapLayerRaiser;
import star.hydrology.events.map.FlowdirectionMapLayerRaiser;
import star.hydrology.events.map.SlopePDFMapLayerEvent;
import star.hydrology.events.map.SlopePDFMapLayerRaiser;
import star.hydrology.events.map.WatershedLayerRaiser;

@SignalComponent()
@Properties( { @Property(name = "flowAcc", type = PaletteRenderableLayer.class), @Property(name = "flowDir", type = PaletteRenderableLayer.class), @Property(name = "watershed", type = PaletteRenderableLayer.class), @Property(name = "layer", type = PaletteRenderableLayer.class, getter = Property.PUBLIC),
        @Property(name = "watershedOrigin", type = Point.class, getter = Property.PUBLIC) })
public class SlopePDFWorker extends SlopePDFWorker_generated implements HeightsRange
{
	private float[] count;
	private float max_height;
	private float min_height;

	private void calculate()
	{
		int[] offsetX = new int[] { 1, 1, 0, -1, -1, -1, 0, 1 };
		int[] offsetY = new int[] { 0, 1, 1, 1, 0, -1, -1, -1 };
		int[] offsetDir = new int[] { 1, 2, 4, 8, 16, 32, 64, 128 };

		if (getFlowAcc() != null && getWatershed() != null && getFlowDir() != null)
		{
			count = new float[90];
			Grid dataset = getWatershed().getDataset();
			Grid flowAcc = getFlowAcc().getDataset();
			Grid flowDir = getFlowDir().getDataset();
			FloatDataset layergrid = null;
			try
			{
				layergrid = (FloatDataset) ((FloatDataset) getWatershed().getDataset()).getSameCoverage(FloatDataset.class);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			int rows = dataset.getRows();
			int cols = dataset.getCols();
			float cellSize = dataset.getCellsize();
			float rad2deg = (float) (180.0 / Math.PI);
			for (int y = 0; y < rows; y++)
			{
				for (int x = 0; x < cols; x++)
				{
					float height = dataset.getElementAt(x, y);
					float accomulation = flowAcc.getElementAt(x, y);

					if (!Float.isNaN(height) && !Float.isNaN(accomulation))
					{
						float next_height = height;
						float distance = cellSize;
						for (int i = 0; i < offsetX.length; i++)
						{
							if (offsetDir[i] == (int) flowDir.getElementAt(x, y))
							{
								next_height = dataset.getElementAt(x + offsetX[i], y + offsetY[i]);
								if (offsetX[i] != 0 || offsetY[i] != 0)
								{
									distance = cellSize * (float) Math.sqrt(2);
								}
								break;
							}
						}
						if (!Float.isNaN(next_height))
						{
							float height_diff = height - next_height;
							int degrees = 0;
							if (distance != 0 && height_diff > 0)
							{
								degrees = (int) (rad2deg * Math.atan(height_diff / distance));
							}
							count[degrees] += 1;
							layergrid.setElementAt(x, y, degrees);
						}
					}
				}
			}
			float normalization_factor = 0;
			for (int i = 0; i != count.length; i++)
			{
				normalization_factor += count[i];
			}
			for (int i = 0; i != count.length; i++)
			{
				count[i] /= normalization_factor;
			}
			ProjectedTerrainMap layer = new ProjectedTerrainMap();
			layer.setLayerName("Slope PDF");
			layer.setDataset(layergrid);
			setLayer(layer);
			raise();
		}
		else
		{
			count = new float[] { 0 };
			setLayer(null);
			raise();
		}
	}

	private void raise()
	{
		(new SlopePDFEvent(this, getLayer() != null)).raise();
		(new SlopePDFMapLayerEvent(this, getLayer() != null)).raise();
	}

	public float getPDF(float height)
	{
		return height >= count.length ? 0 : count[(int) height];
	}

	public float getMaximumHeight()
	{
		return max_height;
	}

	public float getMinimumHeight()
	{
		return min_height;
	}

	@Handles(raises = { SlopePDFRaiser.class, SlopePDFMapLayerRaiser.class }, concurrency = Handles.ASYNC)
	void calculateSlope(final FlowaccumulationMapLayerRaiser flowAcc, final FlowdirectionMapLayerRaiser flowDir, final WatershedLayerRaiser watershed)
	{
		if (watershed.getWatershedOrigin() != null)
		{
			setFlowAcc(flowAcc.getLayer());
			setFlowDir(flowDir.getLayer());
			setWatershed(watershed.getLayer());
			setWatershedOrigin(watershed.getWatershedOrigin());
			calculate();
		}
	}

	public int getLayerKind()
	{
		return LayerConstants.SLOPEPDFLAYER;
	}

}
