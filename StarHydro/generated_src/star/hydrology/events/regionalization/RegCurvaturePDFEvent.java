/* Generated by star.annotations.GeneratedClass, all changes will be lost */

package star.hydrology.events.regionalization;

public class RegCurvaturePDFEvent extends star.hydrology.events.PDFEvent
{
	private static final long serialVersionUID = 1L;

	public  RegCurvaturePDFEvent(star.event.Raiser raiser, boolean valid)
	{
		super( raiser , valid ) ;
	}
	 
	public  RegCurvaturePDFEvent(star.hydrology.events.regionalization.RegCurvaturePDFEvent event)
	{
		super( event ) ;
	}
	 
	public  RegCurvaturePDFEvent(star.hydrology.events.regionalization.RegCurvaturePDFRaiser raiser)
	{
		super( raiser ) ;
	}
	 
	public void raise()
	{
		raiseImpl();
	}
	 
}