/* Generated by star.annotations.GeneratedClass, all changes will be lost */

package star.hydrology.events.map;

public class FilledMapLayerEvent extends star.hydrology.events.map.MapLayerEvent
{
	private static final long serialVersionUID = 1L;

	public  FilledMapLayerEvent(star.event.Raiser raiser, boolean valid)
	{
		super( raiser , valid ) ;
	}
	 
	public  FilledMapLayerEvent(star.hydrology.events.map.FilledMapLayerEvent event)
	{
		super( event ) ;
	}
	 
	public  FilledMapLayerEvent(star.hydrology.events.map.FilledMapLayerRaiser raiser)
	{
		super( raiser ) ;
	}
	 
	public void raise()
	{
		raiseImpl();
	}
	 
}