package app.worker.map;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import plugin.PluginException;
import romi.RomiFactory;
import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.data.layers.ProjectedTerrainMap;
import star.hydrology.events.UnprojectedMapChangedRaiser;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.events.interfaces.UnprojectedMap;
import star.hydrology.events.map.TerrainMapLayerEvent;
import star.hydrology.events.map.TerrainMapLayerRaiser;
import app.server.worker.GISWorker;
import app.worker.AbstractWorker;

@SignalComponent(extend = AbstractWorker.class)
public class ProjectionWorker extends ProjectionWorker_generated
{
	public ProjectionWorker()
	{
		super();
	}

	void updateOutput(final UnprojectedMap map)
	{
		runOnThread(new Runnable()
		{
			public void run()
			{

				try
				{
					GISWorker worker = (GISWorker) RomiFactory.getSystemRomiWrapper(GISWorker.class);
					worker.removeWorkspace();
					worker.makeWorkspace();
					worker.unzip(new File(map.getFilename()));
					worker.project(worker.prefix(new File(map.getFilename())), null);
					ProjectedTerrainMap map = new ProjectedTerrainMap();
					map.setXYZDataset(worker.getProjected());
					map.setLayerName("Projected base map");
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
		return LayerConstants.TERRAIN;
	}

	protected void raise()
	{
		(new TerrainMapLayerEvent(this)).raise();
	}

	@Handles(raises = { TerrainMapLayerRaiser.class }, handleValid = false)
	protected void handleInvalidEvent(UnprojectedMapChangedRaiser raiser1)
	{
		interruptThread();
	}

	@Handles(raises = { TerrainMapLayerRaiser.class })
	protected void handleEvent(UnprojectedMapChangedRaiser raiser1)
	{
		updateOutput(raiser1.getMap());
	}

}
