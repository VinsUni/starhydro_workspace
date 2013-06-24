package starhydro.events;

import star.annotations.Raiser;

@Raiser
public interface ViewScaleChangedRaiser extends star.event.Raiser
{
	float getScale();
}
