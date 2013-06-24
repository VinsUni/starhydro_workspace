package star.hydrology.events;

import star.annotations.Raiser;
import star.hydrology.events.interfaces.UnprojectedMap;

@Raiser
public interface UnprojectedMapChangedRaiser extends star.event.Raiser
{
	public UnprojectedMap getMap();

}
