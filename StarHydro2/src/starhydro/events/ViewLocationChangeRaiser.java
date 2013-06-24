package starhydro.events;

import star.annotations.Raiser;
import starhydro.utils.Point2DInteger;

@Raiser
public interface ViewLocationChangeRaiser extends star.event.Raiser
{
	Point2DInteger getViewLocation();
}
