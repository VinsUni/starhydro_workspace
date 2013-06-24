package star.hydrology.data.interfaces;

import javax.vecmath.Point3f;

public interface GridwStat extends Grid
{
	public float getMinimum();

	public float getMaximum();

	public Point3f getCenter();
}
