package star.hydrology.events;

import star.annotations.Raiser;
import star.hydrology.data.interfaces.AdjustableValue;

@Raiser()
public interface AdjustableValueRaiser extends star.event.Raiser, AdjustableValue
{
	public int getKind();
}
