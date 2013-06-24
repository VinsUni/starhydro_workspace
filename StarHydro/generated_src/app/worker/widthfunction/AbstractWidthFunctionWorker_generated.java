/* Generated by star.annotations.GeneratedClass, all changes will be lost */

package app.worker.widthfunction;

public abstract class AbstractWidthFunctionWorker_generated extends java.lang.Object implements star.event.EventController, star.event.Listener, star.hydrology.events.WidthFunctionRaiser
{
	private star.event.Adapter adapter;
	private static final long serialVersionUID = 1L;

	public  AbstractWidthFunctionWorker_generated()
	{
		super();
	}
	 
	public void addNotify()
	{
		getAdapter().addHandled(star.hydrology.events.StreamRootChangeEvent.class);
	}
	 
	public void eventRaised(final star.event.Event event)
	{
		eventRaisedHandles(event);
	}
	 
	private void eventRaisedHandles(final star.event.Event event)
	{
		if( event.getClass().getName().equals( "star.hydrology.events.StreamRootChangeEvent" ) && event.isValid() ) 
		{
			(new star.hydrology.events.WidthFunctionEvent(this,false)).raise();
		 utils.Runner.runOnThread(new Runnable() { public void run() { 
			 long start = System.nanoTime();
			setRootStream( (star.hydrology.events.StreamRootChangeRaiser)event.getSource());
			 long end = System.nanoTime();
			 if( end - start > 500000000 ) { System.out.println( this.getClass().getName() + ".setRootStream "  + ( end-start )/1000000 ); } }},this,2);
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
	 
	public void raise_WidthFunctionEvent()
	{
		(new star.hydrology.events.WidthFunctionEvent(this)).raise();
	}
	 
	public void removeNotify()
	{
		getAdapter().removeHandled(star.hydrology.events.StreamRootChangeEvent.class);
	}
	 
	abstract void setRootStream(star.hydrology.events.StreamRootChangeRaiser r);
	 
}