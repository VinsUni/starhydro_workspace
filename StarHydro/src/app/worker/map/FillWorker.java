package app.worker.map;

import java.io.FileNotFoundException;
import java.io.IOException;

import plugin.PluginException;
import romi.RomiFactory;
import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.annotations.Threadable;
import star.hydrology.data.layers.ProjectedTerrainMap;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.events.interfaces.PaletteRenderableLayer;
import star.hydrology.events.map.FilledMapLayerEvent;
import star.hydrology.events.map.FilledMapLayerRaiser;
import star.hydrology.events.map.TerrainMapLayerRaiser;
import app.server.worker.GISWorker;
import app.worker.AbstractWorker;

@SignalComponent(extend = AbstractWorker.class)
public class FillWorker extends FillWorker_generated implements Threadable
{
	private void updateOutput(final PaletteRenderableLayer terrain)
	{
		if (terrain != null)
		{
			runOnThread(new Runnable()
			{
				public void run()
				{
					try
					{
						GISWorker worker = (GISWorker) RomiFactory.getSystemRomiWrapper(GISWorker.class);
						worker.fill();
						ProjectedTerrainMap map = new ProjectedTerrainMap();
						map.setXYZDataset(worker.getFilled());
						map.setLayerName("Terrain map filled");
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
	}

	public int getLayerKind()
	{
		return LayerConstants.FILLED;
	}

	@Handles(raises = { FilledMapLayerRaiser.class }, handleValid = false)
	void invalidateFilledTerrain(TerrainMapLayerRaiser raiser)
	{
		interruptThread();
	}

	@Handles(raises = { FilledMapLayerRaiser.class })
	void calculateFilledTerrain(TerrainMapLayerRaiser raiser)
	{
		updateOutput(raiser.getLayer());
	}

	@Override
	protected void raise()
	{
		(new FilledMapLayerEvent(this)).raise();
	}

}
