package app.worker;

import star.annotations.Handles;
import star.event.Adapter;
import star.event.Event;
import star.event.Listener;
import star.hydrology.data.layers.ProjectedTerrainMap;
import star.hydrology.events.LayerChangedEvent;
import star.hydrology.events.LayerChangedRaiser;
import star.hydrology.events.interfaces.PaletteRenderableLayer;

public abstract class AbstractWorker implements Listener, LayerChangedRaiser
{
	private PaletteRenderableLayer map;

	private Adapter adapter = new Adapter(this);

	public Adapter getAdapter()
	{
		return adapter;
	}

	public void addNotify()
	{
		getAdapter().addHandled(LayerChangedEvent.class);
	}

	public void removeNotify()
	{
		getAdapter().removeHandled(LayerChangedEvent.class);
	}

	protected void raise()
	{
		(new LayerChangedEvent(this)).raise();
	}

	protected void setLayer(ProjectedTerrainMap map)
	{
		this.map = map;
		raise();
	}

	public PaletteRenderableLayer getLayer()
	{
		return map;
	}

	public void eventRaised(Event event)
	{
	}

	public abstract int getLayerKind();

	public void runOnThread(final Runnable r)
	{
		utils.Runner.runOnThread(r, this, Handles.ASYNC);
	}

	public void interruptThread()
	{
		utils.Runner.interruptThread(this);
	}
}
