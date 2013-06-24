package app.worker.map;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.data.interfaces.Grid;
import star.hydrology.data.layers.AbstractDataset;
import star.hydrology.data.layers.ByteDataset;
import star.hydrology.data.layers.ProjectedTerrainMap;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.events.interfaces.PaletteRenderableLayer;
import star.hydrology.events.map.FlowUpstreamDirectionEvent;
import star.hydrology.events.map.FlowUpstreamDirectionRaiser;
import star.hydrology.events.map.FlowdirectionMapLayerRaiser;

@SignalComponent()
public class FlowUpstramDirectionWorker extends FlowUpstramDirectionWorker_generated
{
	boolean interrupt = false;
	private PaletteRenderableLayer flowDirection;
	private ProjectedTerrainMap layer;

	boolean isInterrupted()
	{
		if (interrupt || utils.Runner.isInterrupted())
		{
			System.out.println("interrupted");
		}
		return interrupt || utils.Runner.isInterrupted();
	}

	@Handles(raises = { FlowUpstreamDirectionRaiser.class }, handleValid = false)
	public void stop(FlowdirectionMapLayerRaiser flowDirectionRaiser)
	{
		interrupt = true;
	}

	@Handles(raises = { FlowUpstreamDirectionRaiser.class })
	public void calculate(FlowdirectionMapLayerRaiser flowDirectionRaiser)
	{
		System.out.println("calculate me");
		interrupt = false;
		flowDirection = flowDirectionRaiser.getLayer();
		if (flowDirection != null)
		{
			final int[] offsetX = new int[] { 1, 1, 0, -1, -1, -1, 0, 1 };
			final int[] offsetY = new int[] { 0, 1, 1, 1, 0, -1, -1, -1 };
			final byte[] offsetDir = new byte[] { 1, 2, 4, 8, 16, 32, 64, -128 };
			final byte[] offsetUpstream = new byte[] { 1, 2, 4, 8, 16, 32, 64, -128 };
			Grid dataset = flowDirection.getDataset();
			ByteDataset layergrid = null;
			try
			{
				if (dataset instanceof AbstractDataset)
				{
					layergrid = (ByteDataset) ((AbstractDataset) dataset).getSameCoverage(ByteDataset.class);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			int rows = dataset.getRows();
			int cols = dataset.getCols();
			if (isInterrupted())
			{
				System.out.println("return");
				return;
			}
			for (int y = 0; y < rows; y++)
			{
				if (isInterrupted())
				{
					System.out.println("return");
					return;
				}
				for (int x = 0; x < cols; x++)
				{
					for (int i = 0; i < offsetX.length; i++)
					{
						if (offsetDir[i] == (byte) dataset.getElementAt(x, y))
						{
							try
							{
								byte value = (byte) (offsetUpstream[i] | layergrid.getByte(x + offsetX[i], y + offsetY[i]));
								// assert (value != 0);
								layergrid.set(x + offsetX[i], y + offsetY[i], value);
							}
							catch (Exception ex)
							{
							}
							break;
						}
					}
				}
			}
			if (isInterrupted())
			{
				System.out.println("return");
				return;
			}
			ProjectedTerrainMap layer = new ProjectedTerrainMap();
			layer.setLayerName("Flow Upstream Direction");
			layer.setDataset(layergrid);
			this.layer = layer;
			(new FlowUpstreamDirectionEvent(this)).raise();
		}
	}

	public PaletteRenderableLayer getLayer()
	{
		return layer;
	}

	public int getLayerKind()
	{
		return LayerConstants.FLOWDIRUPSTREAM;
	}

}
