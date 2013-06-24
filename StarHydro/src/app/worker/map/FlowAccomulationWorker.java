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
import star.hydrology.events.map.FilledMapLayerRaiser;
import star.hydrology.events.map.FlowaccumulationMapLayerEvent;
import star.hydrology.events.map.FlowaccumulationMapLayerRaiser;
import star.hydrology.events.map.FlowdirectionMapLayerRaiser;
import app.server.worker.GISWorker;
import app.worker.AbstractWorker;

@SignalComponent(extend = AbstractWorker.class)
public class FlowAccomulationWorker extends FlowAccomulationWorker_generated
{
	void updateOutput(final PaletteRenderableLayer terrain, final PaletteRenderableLayer flowdir)
	{
		runOnThread(new Runnable()
		{
			public void run()
			{

				try
				{
					GISWorker worker = (GISWorker) RomiFactory.getSystemRomiWrapper(GISWorker.class);
					worker.flowAccomulation();
					ProjectedTerrainMap map = new ProjectedTerrainMap();
					map.setXYZDataset(worker.getFlowAccumulation());
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

	public int getLayerKind()
	{
		return LayerConstants.FLOWACC;
	}

	@Handles(raises = { FlowaccumulationMapLayerRaiser.class }, handleValid = false)
	protected void invalidate(FilledMapLayerRaiser filledMapRaiser, FlowdirectionMapLayerRaiser flowDir)
	{
		interruptThread();
	}

	@Handles(raises = { FlowaccumulationMapLayerRaiser.class })
	protected void handle(FilledMapLayerRaiser filledMapRaiser, FlowdirectionMapLayerRaiser flowDir)
	{
		updateOutput(filledMapRaiser.getLayer(), flowDir.getLayer());
	}

	@Override
	protected void raise()
	{
		(new FlowaccumulationMapLayerEvent(this)).raise();
	}

}
