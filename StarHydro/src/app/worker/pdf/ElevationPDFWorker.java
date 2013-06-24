package app.worker.pdf;

import java.awt.Point;

import star.annotations.Handles;
import star.annotations.Properties;
import star.annotations.Property;
import star.annotations.SignalComponent;
import star.hydrology.data.interfaces.Grid;
import star.hydrology.events.ElevationPDFEvent;
import star.hydrology.events.ElevationPDFRaiser;
import star.hydrology.events.interfaces.HeightsRange;
import star.hydrology.events.interfaces.PaletteRenderableLayer;
import star.hydrology.events.map.FlowaccumulationMapLayerRaiser;
import star.hydrology.events.map.FlowdirectionMapLayerRaiser;
import star.hydrology.events.map.WatershedLayerRaiser;

@SignalComponent()
@Properties( { @Property(name = "flowAcc", type = PaletteRenderableLayer.class), @Property(name = "flowDir", type = PaletteRenderableLayer.class), @Property(name = "watershed", type = PaletteRenderableLayer.class), @Property(name = "watershedOrigin", type = Point.class, getter = Property.PUBLIC) })
public class ElevationPDFWorker extends ElevationPDFWorker_generated implements HeightsRange
{
	private float[] count;
	private float max_height;
	private float min_height;

	@Handles(raises = { ElevationPDFRaiser.class }, concurrency = Handles.ASYNC)
	void calculateElevation(final FlowaccumulationMapLayerRaiser flowAcc, final FlowdirectionMapLayerRaiser flowDir, final WatershedLayerRaiser watershed)
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

	private void calculate()
	{
		int[] offsetX = new int[] { 1, 1, 0, -1, -1, -1, 0, 1 };
		int[] offsetY = new int[] { 0, 1, 1, 1, 0, -1, -1, -1 };
		int[] offsetDir = new int[] { 1, 2, 4, 8, 16, 32, 64, 128 };

		if (getFlowAcc() != null && getWatershed() != null && getFlowDir() != null)
		{
			count = new float[MAX_HEIGHT - MIN_HEIGHT];
			max_height = Float.MIN_VALUE;
			min_height = Float.MAX_VALUE;
			Grid dataset = getWatershed().getDataset();
			Grid flowAcc = getFlowAcc().getDataset();
			Grid flowDir = getFlowDir().getDataset();
			int rows = dataset.getRows();
			int cols = dataset.getCols();
			for (int y = 0; y < rows; y++)
			{
				for (int x = 0; x < cols; x++)
				{
					float height = dataset.getElementAt(x, y);
					float accomulation = flowAcc.getElementAt(x, y);

					if (!Float.isNaN(height) && !Float.isNaN(accomulation))
					{
						float next_height = height;
						for (int i = 0; i < offsetX.length; i++)
						{
							if (offsetDir[i] == (int) flowDir.getElementAt(x, y))
							{
								next_height = dataset.getElementAt(x + offsetX[i], y + offsetY[i]);
								break;
							}
						}
						if (!Float.isNaN(next_height))
						{
							int ithis = (int) height;
							int inext = (int) next_height;
							if (ithis != inext)
							{
								if (height < MAX_HEIGHT && height > MIN_HEIGHT)
								{
									float rate = (ithis - inext <= 1) ? 1.0f : 1.0f / (int) (ithis - inext);
									for (int hg = ithis; hg > inext; hg--)
									{
										count[(int) hg] += rate;
									}
									max_height = max_height > height ? max_height : height;
									min_height = min_height < height ? min_height : height;
								}
								else
								{
									System.out.println("Height out of bounds - ignoring. ");
								}
							}
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

			raise();
		}
		else
		{
			count = new float[] { 0 };
			max_height = 0;
			min_height = 0;
			raise();
		}
	}

	private void raise()
	{
		(new ElevationPDFEvent(this, count.length != 1)).raise();
	}

	public float getPDF(float height)
	{
		return count[(int) height];
	}

	public float getMaximumHeight()
	{
		return max_height;
	}

	public float getMinimumHeight()
	{
		return min_height;
	}

}
