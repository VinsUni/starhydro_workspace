/* Generated by star.annotations.GeneratedClass, all changes will be lost */

package star.hydrology.ui.chart.horton;

public class TrendLineEvent extends star.event.Event
{
	private static final long serialVersionUID = 1L;

	public  TrendLineEvent(star.event.Raiser raiser, boolean valid)
	{
		super( raiser , valid ) ;
	}
	 
	public  TrendLineEvent(star.hydrology.ui.chart.horton.TrendLineEvent event)
	{
		super( event ) ;
	}
	 
	public  TrendLineEvent(star.hydrology.ui.chart.horton.TrendLineRaiser raiser)
	{
		super( raiser ) ;
	}
	 
	public void raise()
	{
		raiseImpl();
	}
	 
}