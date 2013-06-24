package starhydro.data.impl;

import starhydro.data.interfaces.FloatGridWritable;
import starhydro.data.interfaces.FloatRange;

public class FloatGridManager extends GridManager<FloatGridWritable> implements FloatGridWritable
{
	public FloatGridManager(final String name, final int elementsPerGrid)
	{
		super(new FloatGridWritable[360][180], elementsPerGrid, name);
	}

	public void set(final int x, final int y, final float value)
	{
		Coord4D c = getCoordinates(x, y);
		if( hasTile(c.xx, c.yy) )
		{
			elements[c.xx][c.yy].set(c.x, c.y, value);
		}
	}

	public float get(final int x, final int y)
	{
		Coord4D c = getCoordinates(x, y);
		FloatGridWritable tile = elements[c.xx][c.yy];
		return tile != null ? tile.get(c.x, c.y) : Float.NaN;
	}

	public float[] get3x3(final int x, final int y)
	{
		float[] ret = new float[8];
		ret[0] = get(x + 1, y + 0);
		ret[1] = get(x + 1, y + 1);
		ret[2] = get(x + 0, y + 1);
		ret[3] = get(x - 1, y + 1);
		ret[4] = get(x - 1, y + 0);
		ret[5] = get(x - 1, y - 1);
		ret[6] = get(x + 0, y - 1);
		ret[7] = get(x + 1, y - 1);
		return ret;
	}

	public FloatRange getValueRange(final int x, final int y)
	{
		if( x >=0 && x < 360 && y >= 0 && y < 180 && elements[x][y] != null )
		{
			return elements[x][y].getValueRange();
		}
		else
		{
			return FloatRangeImpl.getNaNRange();
		}
	}

	public FloatRange getValueRange()
	{
		throw new RuntimeException("NOT IMPLEMENTED");
	}

}
