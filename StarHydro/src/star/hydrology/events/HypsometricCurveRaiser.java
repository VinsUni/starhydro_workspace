package star.hydrology.events;

import star.annotations.Raiser;

@Raiser()
public interface HypsometricCurveRaiser extends star.event.Raiser
{
	float getMinimumHeight();

	float getMaximumHeight();

	float getAccomulationAt(float height);
}
