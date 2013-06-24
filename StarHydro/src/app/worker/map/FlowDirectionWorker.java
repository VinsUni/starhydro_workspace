package app.worker.map;

import java.io.FileNotFoundException;
import java.io.IOException;

import plugin.PluginException;
import romi.RomiFactory;
import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.data.layers.ProjectedTerrainMap;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.events.interfaces.PaletteRenderableLayer;
import star.hydrology.events.map.FlowdirectionMapLayerEvent;
import star.hydrology.events.map.FlowdirectionMapLayerRaiser;
import star.hydrology.events.map.TerrainMapLayerRaiser;
import app.server.worker.GISWorker;
import app.worker.AbstractWorker;

@SignalComponent(extend = AbstractWorker.class)
public class FlowDirectionWorker extends FlowDirectionWorker_generated
{
	public int getLayerKind()
	{
		return LayerConstants.FLOWDIR;
	}

	void updateOutput(final PaletteRenderableLayer terrain)
	{
		runOnThread(new Runnable()
		{
			public void run()
			{

				try
				{
					GISWorker worker = (GISWorker) RomiFactory.getSystemRomiWrapper(GISWorker.class);
					worker.flowDirection();
					ProjectedTerrainMap map = new ProjectedTerrainMap();
					map.setXYZDataset(worker.getFlowDirection());
					map.setLayerName("Flow direction");
					setLayer(map);
				}
				catch (FileNotFoundException e)
				{
					e.printStackTrace();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				catch (PluginException e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	@Handles(raises = { FlowdirectionMapLayerRaiser.class }, handleValid = false)
	protected void invalidateFlowDirection(TerrainMapLayerRaiser raiser)
	{
		interruptThread();
	}

	@Handles(raises = { FlowdirectionMapLayerRaiser.class })
	protected void calculateFlowDirection(TerrainMapLayerRaiser raiser)
	{
		updateOutput(raiser.getLayer());
	}

	protected void raise()
	{
		(new FlowdirectionMapLayerEvent(this)).raise();
	}

}
