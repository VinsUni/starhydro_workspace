package star.hydrology.events;

import star.annotations.Raiser;

@Raiser()
public interface MainStreamLengthRaiser extends star.event.Raiser
{
	float getLength();
}
