package star.hydrology.events;

import javax.vecmath.Point3f;

import star.annotations.Raiser;

@Raiser
public interface SelectWatershedOriginRaiser extends star.event.Raiser
{
	public Point3f getPoint();
}
