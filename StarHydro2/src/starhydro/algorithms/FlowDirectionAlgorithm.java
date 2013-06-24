package starhydro.algorithms;

import java.util.ArrayList;

import starhydro.data.impl.ByteGridManager;
import starhydro.data.impl.FloatGridManager;
import starhydro.data.interfaces.ByteGridWritable;
import starhydro.utils.Rectangle2DInteger;

public class FlowDirectionAlgorithm
{
	ByteGridWritable direction = null;

	public void calculate(ArrayList<Rectangle2DInteger> rectArray, FloatGridManager heights, ByteGridManager direction)
	{
		this.direction = direction;
		for (Rectangle2DInteger reprocessRange : rectArray)
		{
			int xfrom = reprocessRange.getX();
			int xto = reprocessRange.getWidth() + xfrom;
			int yfrom = reprocessRange.getY();
			int yto = reprocessRange.getHeight() + yfrom;
			for (int yy = yfrom; yy < yto; yy++)
			{
				for (int xx = xfrom; xx < xto; xx++)
				{
					execute(xx, yy, heights.get(xx, yy), heights.get3x3(xx, yy));
				}
			}
		}
	}

	private void execute(int x, int y, float value, float[] data)
	{
		byte direction = getFlowDirection(value, data);
		setFlowDirection(x, y, direction);
	}

	private byte getFlowDirection(float value, float[] data)
	{
		byte direction = 0;
		float min = value;
		for (int i = 0; i < data.length; i++)
		{
			if( min > data[i] )
			{
				min = data[i];
				direction = (byte) (1 << i);
			}
		}
		return direction;
	}

	private void setFlowDirection(int x, int y, byte value)
	{
		direction.set(x, y, value);
	}

}
