package starhydro.data.impl;

import starhydro.data.interfaces.IntegerRange;

public class IntegerRangeImpl implements IntegerRange
{
	private int min = Integer.MAX_VALUE;
	private int max = Integer.MIN_VALUE;

	public int getMax()
	{
		return max;
	}

	public int getMin()
	{
		return min;
	}

	public void addValue(final int value)
	{
		min = min > value ? value : min;
		max = max < value ? value : max;
	}

	public void addRange(final IntegerRange range)
	{
		addValue(range.getMin());
		addValue(range.getMax());
	}

	public String toString()
	{
		return "[" + min + "," + max + "]";

	}
}
