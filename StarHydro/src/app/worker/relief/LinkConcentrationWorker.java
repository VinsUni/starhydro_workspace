package app.worker.relief;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.data.interfaces.Grid;
import star.hydrology.events.GridStatisticsProviderChangeRaiser;
import star.hydrology.events.LinkConcentrationEvent;
import star.hydrology.events.LinkConcentrationRaiser;
import star.hydrology.events.interfaces.HeightsRange;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.events.interfaces.PaletteRenderableLayer;
import star.hydrology.events.map.FlowaccumulationMapLayerRaiser;
import star.hydrology.events.map.FlowdirectionMapLayerRaiser;
import star.hydrology.events.map.WatershedLayerRaiser;

@SignalComponent()
public class LinkConcentrationWorker extends LinkConcentrationWorker_generated implements HeightsRange
{
	private PaletteRenderableLayer flowAcc;
	private PaletteRenderableLayer flowDir;
	private PaletteRenderableLayer watershed;
	private float threshold = 256;

	private int[] count;
	private float max_height;
	private float min_height;
	private boolean calculate = false;

	@Handles(raises = { LinkConcentrationRaiser.class }, handleValid = false)
	void handleInvalid(WatershedLayerRaiser watershed, FlowaccumulationMapLayerRaiser flowAcc, FlowdirectionMapLayerRaiser flowDir)
	{
		calculate = false;
	}

	@Handles(raises = { LinkConcentrationRaiser.class })
	void handle(WatershedLayerRaiser watershed, FlowaccumulationMapLayerRaiser flowAcc, FlowdirectionMapLayerRaiser flowDir)
	{
		setFlowAcc(flowAcc.getLayer());
		setFlowDir(flowDir.getLayer());
		setWatershed(watershed.getLayer());
		calculate = true;
		calculate();
	}

	@Handles(raises = {})
	void setAdjustableValue(GridStatisticsProviderChangeRaiser r)
	{
		if (r.getKind() == LayerConstants.STREAMS)
		{
			setThreshold(r.getCurrent());
			if (calculate)
			{
				calculate();
			}
		}
	}

	private void setThreshold(float x)
	{
		this.threshold = (int) x;
	}

	private PaletteRenderableLayer getWatershed()
	{
		return watershed;
	}

	private void setWatershed(PaletteRenderableLayer watershed)
	{
		this.watershed = watershed;
	}

	private PaletteRenderableLayer getFlowAcc()
	{
		return flowAcc;
	}

	private void setFlowAcc(PaletteRenderableLayer flowAcc)
	{
		this.flowAcc = flowAcc;
	}

	private PaletteRenderableLayer getFlowDir()
	{
		return flowDir;
	}

	private void setFlowDir(PaletteRenderableLayer flowDir)
	{
		this.flowDir = flowDir;
	}

	private void calculate()
	{
		int[] offsetX = new int[] { 1, 1, 0, -1, -1, -1, 0, 1 };
		int[] offsetY = new int[] { 0, 1, 1, 1, 0, -1, -1, -1 };
		int[] offsetDir = new int[] { 1, 2, 4, 8, 16, 32, 64, 128 };

		count = new int[MAX_HEIGHT - MIN_HEIGHT];
		max_height = Float.MIN_VALUE;
		min_height = Float.MAX_VALUE;
		Grid dataset = getWatershed().getDataset();
		Grid flowAcc = getFlowAcc().getDataset();
		Grid flowDir = getFlowDir().getDataset();
		int rows = dataset.getRows();
		int cols = dataset.getCols();
		boolean raise = false;
		for (int y = 0; y < rows; y++)
		{
			for (int x = 0; x < cols; x++)
			{
				float height = dataset.getElementAt(x, y);
				float accomulation = flowAcc.getElementAt(x, y);
				if (!(accomulation < threshold))
				{
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
									for (int hg = ithis; hg > inext; hg--)
									{
										count[(int) hg]++;
									}
									max_height = max_height > height ? max_height : height;
									min_height = min_height < height ? min_height : height;
									raise = true;
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
		}
		if (!raise)
		{
			max_height = Float.NaN;
			min_height = Float.NaN;
		}
		raise();
	}

	private void raise()
	{
		(new LinkConcentrationEvent(this)).raise();
	}

	public int getAccomulationAt(float height)
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
