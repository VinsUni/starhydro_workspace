/* Generated by star.annotations.GeneratedClass, all changes will be lost */

package star.hydrology.events;

public class StreamRootChangeEvent extends star.event.Event
{
	private static final long serialVersionUID = 1L;

	public  StreamRootChangeEvent(star.event.Raiser raiser, boolean valid)
	{
		super( raiser , valid ) ;
	}
	 
	public  StreamRootChangeEvent(star.hydrology.events.StreamRootChangeEvent event)
	{
		super( event ) ;
	}
	 
	public  StreamRootChangeEvent(star.hydrology.events.StreamRootChangeRaiser raiser)
	{
		super( raiser ) ;
	}
	 
	public void raise()
	{
		raiseImpl();
	}
	 
}