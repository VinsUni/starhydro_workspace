/* Generated by star.annotations.GeneratedClass, all changes will be lost */

package app.viewers;

public abstract class AbstractMapViewer_generated extends java.lang.Object implements star.event.EventController, star.event.Listener, star.hydrology.events.RenderableVisibleRaiser, star.hydrology.events.ViewerChangeRaiser
{
	private star.event.Adapter adapter;
	private static final long serialVersionUID = 1L;

	public  AbstractMapViewer_generated()
	{
		super();
	}
	 
	public void addNotify()
	{
		getAdapter().addHandled(star.hydrology.events.map.FilledMapLayerEvent.class);
		getAdapter().addHandled(star.hydrology.events.tribs.AnimationEvent.class);
		getAdapter().addHandled(star.hydrology.events.LayerChangedEvent.class);
		getAdapter().addHandled(star.hydrology.events.VisibilityChangedEvent.class);
		getAdapter().addHandled(star.hydrology.events.PaletteChangedEvent.class);
	}
	 
	public void eventRaised(final star.event.Event event)
	{
		eventRaisedHandles(event);
	}
	 
	private void eventRaisedHandles(final star.event.Event event)
	{
		if( event.getClass().getName().equals( "star.hydrology.events.map.FilledMapLayerEvent" ) && event.isValid() ) 
		{
			 utils.Runner.runOnThread(new Runnable() { public void run() { 
			 long start = System.nanoTime();
			updateTerrain( (star.hydrology.events.map.FilledMapLayerRaiser)event.getSource());
			 long end = System.nanoTime();
			 if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".updateTerrain "  + ( end-start )/1000000 ); } }},this,2);
		}if( event.getClass().getName().equals( "star.hydrology.events.tribs.AnimationEvent" ) && event.isValid() ) 
		{
			 utils.Runner.runOnThread(new Runnable() { public void run() { 
			 long start = System.nanoTime();
			handleAnimation( (star.hydrology.events.tribs.AnimationRaiser)event.getSource());
			 long end = System.nanoTime();
			 if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".handleAnimation "  + ( end-start )/1000000 ); } }},this,2);
		}if( event.getClass().getName().equals( "star.hydrology.events.LayerChangedEvent" ) && event.isValid() ) 
		{
			(new star.hydrology.events.ViewerChangeEvent(this,false)).raise();
		 utils.Runner.runOnThread(new Runnable() { public void run() { 
			 long start = System.nanoTime();
			updateLayers( (star.hydrology.events.LayerChangedRaiser)event.getSource());
			 long end = System.nanoTime();
			 if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".updateLayers "  + ( end-start )/1000000 ); } }},this,2);
		}if( event.getClass().getName().equals( "star.hydrology.events.VisibilityChangedEvent" ) && event.isValid() ) 
		{
			 utils.Runner.runOnThread(new Runnable() { public void run() { 
			 long start = System.nanoTime();
			updateVisibility( (star.hydrology.events.VisibilityChangedRaiser)event.getSource());
			 long end = System.nanoTime();
			 if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".updateVisibility "  + ( end-start )/1000000 ); } }},this,2);
		}if( event.getClass().getName().equals( "star.hydrology.events.PaletteChangedEvent" ) && event.isValid() ) 
		{
			 utils.Runner.runOnThread(new Runnable() { public void run() { 
			 long start = System.nanoTime();
			updatePalette( (star.hydrology.events.PaletteChangedRaiser)event.getSource());
			 long end = System.nanoTime();
			 if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".updatePalette "  + ( end-start )/1000000 ); } }},this,2);
		}
	}
	 
	public star.event.Adapter getAdapter()
	{
		if( adapter == null )
		{
			adapter = new star.event.Adapter(this);
		}
		return adapter;
	}
	 
	abstract void handleAnimation(star.hydrology.events.tribs.AnimationRaiser r);
	 
	public void raise_RenderableVisibleEvent()
	{
		(new star.hydrology.events.RenderableVisibleEvent(this)).raise();
	}
	 
	public void raise_ViewerChangeEvent()
	{
		(new star.hydrology.events.ViewerChangeEvent(this)).raise();
	}
	 
	public void removeNotify()
	{
		getAdapter().removeHandled(star.hydrology.events.map.FilledMapLayerEvent.class);
		getAdapter().removeHandled(star.hydrology.events.tribs.AnimationEvent.class);
		getAdapter().removeHandled(star.hydrology.events.LayerChangedEvent.class);
		getAdapter().removeHandled(star.hydrology.events.VisibilityChangedEvent.class);
		getAdapter().removeHandled(star.hydrology.events.PaletteChangedEvent.class);
	}
	 
	abstract void updateLayers(star.hydrology.events.LayerChangedRaiser r);
	 
	abstract void updatePalette(star.hydrology.events.PaletteChangedRaiser raiser);
	 
	abstract void updateTerrain(star.hydrology.events.map.FilledMapLayerRaiser raiser);
	 
	abstract void updateVisibility(star.hydrology.events.VisibilityChangedRaiser raiser);
	 
}