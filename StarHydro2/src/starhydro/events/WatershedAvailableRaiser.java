package starhydro.events;

import star.annotations.Raiser;
import starhydro.model.FloatWorld;

@Raiser
public interface WatershedAvailableRaiser extends star.event.Raiser
{
	public FloatWorld getView2D();
}
