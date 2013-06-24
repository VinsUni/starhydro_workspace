package starhydro.data.impl;

import starhydro.data.interfaces.Factory;
import starhydro.data.interfaces.FloatGridWritable;
import starhydro.data.interfaces.FloatRange;

public class FloatGrid1D implements FloatGridWritable
{
	private final int rows;
	private final int cols;
	private final float[] data;

	public static Factory<FloatGridWritable> getFactory(final int x, final int y)
	{
		return new Factory<FloatGridWritable>()
		{
			public FloatGridWritable get()
			{
				return new FloatGrid1D(x, y);
			}
		};
	}

	public FloatGrid1D(final int rows, final int cols, final float[] data)
	{
		this.rows = rows;
		this.cols = cols;
		this.data = data;
		if( data.length < rows * cols )
		{
			throw new RuntimeException("Data too short for length & height");
		}
	}

	public FloatGrid1D(final int rows, final int cols)
	{
		this.rows = rows;
		this.cols = cols;
		this.data = new float[rows * cols];
		java.util.Arrays.fill(data, Float.NaN);
	}

	public int getRows()
	{
		return rows;
	}

	public int getCols()
	{
		return cols;
	}

	public float getCellsize()
	{
		return Float.NaN;
	}

	public float get(final int x, final int y)
	{
		return data[x + y * cols];
	}

	public float[] get3x3(int x, int y)
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

	public void set(final int x, final int y, final float value)
	{
		data[x + y * cols] = value;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[FlotGrid1D\n");
		int rows = getRows();
		int cols = getCols();
		for (int y = 0; y < rows; y++)
		{
			sb.append('\t');
			for (int x = 0; x < cols; x++)
			{
				sb.append(get(x, y));
				sb.append(',');
			}
			sb.append('\n');
		}
		sb.append("]");
		return sb.toString();
	}

	public FloatRange getValueRange()
	{
		FloatRangeImpl range = new FloatRangeImpl();
		for (int i = 0; i < data.length; i++)
		{
			range.addValue(data[i]);
		}
		return range;
	}
}
