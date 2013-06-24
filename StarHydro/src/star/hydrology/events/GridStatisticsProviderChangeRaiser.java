package star.hydrology.events;

import star.annotations.Raiser;

@Raiser()
public interface GridStatisticsProviderChangeRaiser extends star.event.Raiser
{
	float getCurrent();

	int getKind();
}
