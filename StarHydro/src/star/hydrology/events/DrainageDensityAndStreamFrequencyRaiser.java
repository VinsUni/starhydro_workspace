package star.hydrology.events;

import star.annotations.Raiser;

@Raiser()
public interface DrainageDensityAndStreamFrequencyRaiser extends star.event.Raiser
{
	public float getStreamFrequency();

	public float getDrainageDensity();
}
