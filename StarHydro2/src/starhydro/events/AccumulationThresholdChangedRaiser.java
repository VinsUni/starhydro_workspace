package starhydro.events;

import star.annotations.Raiser;

@Raiser
public interface AccumulationThresholdChangedRaiser extends star.event.Raiser
{
	float getAccumulation();
}
