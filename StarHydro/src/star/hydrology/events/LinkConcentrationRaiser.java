package star.hydrology.events;

import star.annotations.Raiser;

@Raiser()
public interface LinkConcentrationRaiser extends star.event.Raiser
{
	float getMinimumHeight();

	float getMaximumHeight();

	int getAccomulationAt(float height);

}
