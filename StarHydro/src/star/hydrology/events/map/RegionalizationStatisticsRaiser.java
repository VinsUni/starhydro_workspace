package star.hydrology.events.map;

import star.annotations.Raiser;
import app.viewers.map.RegionalizationModel;

@Raiser()
public interface RegionalizationStatisticsRaiser extends star.event.Raiser
{
	public RegionalizationModel getStatistics();

}
