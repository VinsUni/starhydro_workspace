package app.worker.pdf;

import java.awt.Point;

import star.annotations.Handles;
import star.annotations.Properties;
import star.annotations.Property;
import star.annotations.SignalComponent;
import star.hydrology.data.interfaces.Grid;
import star.hydrology.data.layers.FloatDataset;
import star.hydrology.data.layers.ProjectedTerrainMap;
import star.hydrology.events.CurvaturePDFEvent;
import star.hydrology.events.CurvaturePDFRaiser;
import star.hydrology.events.interfaces.HeightsRange;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.events.interfaces.PaletteRenderableLayer;
import star.hydrology.events.map.CurvaturePDFLMapLayerEvent;
import star.hydrology.events.map.CurvaturePDFLMapLayerRaiser;
import star.hydrology.events.map.FlowaccumulationMapLayerRaiser;
import star.hydrology.events.map.FlowdirectionMapLayerRaiser;
import star.hydrology.events.map.WatershedLayerRaiser;
import utils.ArrayNumerics;

@SignalComponent()
@Properties( { @Property(name = "flowAcc", type = PaletteRenderableLayer.class), @Property(name = "flowDir", type = PaletteRenderableLayer.class), @Property(name = "watershed", type = PaletteRenderableLayer.class), @Property(name = "layer", type = PaletteRenderableLayer.class, getter = Property.PUBLIC),
        @Property(name = "watershedOrigin", type = Point.class) })
public class CurvaturePDFWorker extends CurvaturePDFWorker_generated implements HeightsRange
{
	private float[] count;
	private float max_height;
	private float min_height;

	private final int[] offsetX = new int[] { 1, 1, 0, -1, -1, -1, 0, 1 };
	private final int[] offsetY = new int[] { 0, 1, 1, 1, 0, -1, -1, -1 };
	private final int[] offsetDir = new int[] { 1, 2, 4, 8, 16, 32, 64, 128 };

	private void getNextPoint(Grid flowDir, Point in)
	{
		int value = (int) flowDir.getElementAt(in.x, in.y);
		for (int i = 0; i < offsetX.length; i++)
		{
			if (offsetDir[i] == value)
			{
				in.x += offsetX[i];
				in.y += offsetY[i];
				return;
			}
		}
	}

	private float distance(float x, float y)
	{
		return (float) Math.sqrt(x * x + y * y);
	}

	private void calculate()
	{
		if (getFlowAcc() != null && getWatershed() != null && getFlowDir() != null)
		{
			count = new float[MAX_HEIGHT - MIN_HEIGHT];
			min_height = MAX_HEIGHT;
			max_height = MIN_HEIGHT;
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
			Point next_point = new Point();
			Point next2_point = new Point();
			for (int y = 0; y < rows; y++)
			{
				if (utils.Runner.isInterrupted())
				{
					return;
				}
				for (int x = 0; x < cols; x++)
				{
					float height = dataset.getElementAt(x, y);
					float accomulation = flowAcc.getElementAt(x, y);

					if (!Float.isNaN(height) && !Float.isNaN(accomulation))
					{
						next_point.x = x;
						next_point.y = y;
						getNextPoint(flowDir, next_point);

						float next_height = dataset.getElementAt(next_point.x, next_point.y);
						if (!(Float.isNaN(next_height) || (next_point.x == x && next_point.y == y)))
						{
							float distance = cellSize * distance(next_point.x - x, next_point.y - y);

							next2_point.x = next_point.x;
							next2_point.y = next_point.y;
							getNextPoint(flowDir, next2_point);

							float next2_height = dataset.getElementAt(next2_point.x, next2_point.y);
							if (!(Float.isNaN(next2_height) || (next_point.x == next2_point.x && next_point.y == next2_point.y)))
							{
								float distance2 = cellSize * distance(next_point.x - next2_point.x, next_point.y - next2_point.y);

								if (!Float.isNaN(next_height) && !Float.isNaN(next2_height) && distance != 0 && distance2 != 0)
								{
									float h1 = height - next_height;
									float h2 = next_height - next2_height;
									float s1 = h1 / distance;
									float s2 = h2 / distance2;
									float curvature = 2 * (s1 - s2) / (distance + distance2) * 10000;
									min_height = Math.min(curvature, min_height);
									max_height = Math.max(curvature, max_height);
									count[(int) (curvature - MIN_HEIGHT)] += 1;
									layergrid.setElementAt(x, y, curvature);
								}
							}
						}
					}

				}
			}
			ArrayNumerics.normalize(count);
			ProjectedTerrainMap layer = new ProjectedTerrainMap();
			layer.setLayerName("Curvature PDF");
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
		(new CurvaturePDFEvent(this, getLayer() != null)).raise();
		(new CurvaturePDFLMapLayerEvent(this, getLayer() != null)).raise();
	}

	public float getPDF(float height)
	{
		int index = (int) (height - MIN_HEIGHT);
		if (index > 0 && index < count.length)
		{
			return count[(int) index];
		}
		return 0;
	}

	public float getMaximumHeight()
	{
		return max_height;
	}

	public float getMinimumHeight()
	{
		return min_height;
	}

	public int getLayerKind()
	{
		return LayerConstants.CURVATUREPDFLAYER;
	}

	@Handles(raises = { CurvaturePDFRaiser.class, CurvaturePDFLMapLayerRaiser.class }, concurrency = Handles.ASYNC)
	protected void calculateCurvature(final FlowaccumulationMapLayerRaiser flowAcc, final FlowdirectionMapLayerRaiser flowDir, final WatershedLayerRaiser watershed)
	{
		if (watershed.getWatershedOrigin() != null)
		{
			if (utils.Runner.isInterrupted())
			{
				return;
			}
			setFlowAcc(flowAcc.getLayer());
			setFlowDir(flowDir.getLayer());
			setWatershed(watershed.getLayer());
			setWatershedOrigin(watershed.getWatershedOrigin());
			if (utils.Runner.isInterrupted())
			{
				return;
			}
			calculate();
		}
	}

}
