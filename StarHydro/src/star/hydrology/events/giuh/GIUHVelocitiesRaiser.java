package star.hydrology.events.giuh;

import star.annotations.Raiser;

@Raiser
public interface GIUHVelocitiesRaiser extends star.event.Raiser
{
	float getHillslopeVelocity();

	float getChannelVelocity();

}
