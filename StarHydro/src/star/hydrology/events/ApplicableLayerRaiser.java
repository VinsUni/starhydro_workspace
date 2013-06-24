package star.hydrology.events;

import star.annotations.Raiser;

@Raiser
public interface ApplicableLayerRaiser extends star.event.Raiser
{
	final static String watershedSetup = "Watershed Setup";
	final static String planar = "Planar";
	final static String topography = "Topography";
	final static String relief = "Relief";
	final static String hydrographic = "Hydrographic";
	final static String pdf = "PDF";
	final static String regionalization = "Regionalization";
	final static String rainGauges = "Rain Gauges";
	final static String other = "Other";

	int[] getLayers();

	String getName();
}
