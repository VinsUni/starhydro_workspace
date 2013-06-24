package star.hydrology.events;

import star.annotations.Raiser;
import star.hydrology.events.interfaces.Visible;

@Raiser
public interface VisibilityChangedRaiser extends Visible, star.event.Raiser
{

}
