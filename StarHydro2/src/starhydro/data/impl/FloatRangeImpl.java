package starhydro.data.impl;

import starhydro.data.interfaces.FloatRange;

public class FloatRangeImpl implements FloatRange
{
	private float min = Float.MAX_VALUE;
	private float max = Float.MIN_VALUE;

	public float getMax()
	{
		return max;
	}

	public float getMin()
	{
		return min;
	}

	public void addValue(final float value)
	{
		min = min > value ? value : min;
		max = max < value ? value : max;
	}

	public void addRange(final FloatRange range)
	{
		addValue(range.getMin());
		addValue(range.getMax());
	}

	@Override
	public String toString()
	{
		return "[" + min + "," + max + "]";
	}
	
	static FloatRangeImpl NaNRange ;
	static FloatRange getNaNRange()
	{
		if( NaNRange == null )
		{
			NaNRange = new FloatRangeImpl();
			NaNRange.min = Float.NaN ;
			NaNRange.max = Float.NaN ;
		}
		return NaNRange;
	}
}
