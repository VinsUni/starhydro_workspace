/* Generated by star.annotations.GeneratedClass, all changes will be lost */

package star.hydrology.events.map;

public class RegionalizeWorkerDiscreteChannelLayerEvent extends star.hydrology.events.map.MapLayerEvent
{
	private static final long serialVersionUID = 1L;

	public  RegionalizeWorkerDiscreteChannelLayerEvent(star.event.Raiser raiser, boolean valid)
	{
		super( raiser , valid ) ;
	}
	 
	public  RegionalizeWorkerDiscreteChannelLayerEvent(star.hydrology.events.map.RegionalizeWorkerDiscreteChannelLayerEvent event)
	{
		super( event ) ;
	}
	 
	public  RegionalizeWorkerDiscreteChannelLayerEvent(star.hydrology.events.map.RegionalizeWorkerDiscreteChannelLayerRaiser raiser)
	{
		super( raiser ) ;
	}
	 
	public void raise()
	{
		raiseImpl();
	}
	 
}