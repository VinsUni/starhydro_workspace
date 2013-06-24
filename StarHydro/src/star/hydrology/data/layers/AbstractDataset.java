package star.hydrology.data.layers;

import java.io.Serializable;

import javax.vecmath.Point3f;

public abstract class AbstractDataset implements Serializable
{
	private static final long serialVersionUID = 1L;
	private Point3f center;
	private int rows;
	private int cols;
	private float cellsize;

	public void setSize(int cols, int rows)
	{
		this.rows = rows;
		this.cols = cols;
	}

	public int getCols()
	{

		return cols;
	}

	public int getRows()
	{
		return rows;
	}

	public Point3f getCenter()
	{
		return center;
	}

	public void setCenter(Point3f center)
	{
		this.center = center;
	}

	public void setCellsize(float cellsize)
	{
		this.cellsize = cellsize;
	}

	public float getCellsize()
	{
		return cellsize;
	}

	public AbstractDataset getSameCoverage(Class c) throws InstantiationException, IllegalAccessException
	{
		AbstractDataset ret = (AbstractDataset) c.newInstance();
		ret.setCellsize(this.getCellsize());
		ret.setSize(getCols(), getRows());
		ret.setCenter(getCenter());
		return ret;
	}

}
