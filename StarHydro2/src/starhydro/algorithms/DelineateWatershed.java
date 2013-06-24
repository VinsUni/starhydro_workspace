package starhydro.algorithms;

import java.util.ArrayList;
import java.util.Stack;

import starhydro.data.interfaces.ByteGrid;
import starhydro.data.interfaces.ByteGridWritable;
import starhydro.data.interfaces.FloatGrid;
import starhydro.utils.FlowDirectionUtils;
import starhydro.utils.Point2DInteger;

public class DelineateWatershed
{
	public void calculate(int x, int y, ByteGridWritable watershed, ByteGrid directions, FloatGrid accumulation, float threshold)
	{
		Point2DInteger outlet = goDownstream(x, y, threshold, directions, accumulation);
		delineate(outlet, watershed, directions);
	}

	private Point2DInteger goDownstream(int x, int y, float threshold, ByteGrid directions, FloatGrid accumulation)
	{
		int[] offset = new int[2];
		float currentAccumulation = accumulation.get(x, y);
		while( true )
		{
			FlowDirectionUtils.getDownstreamOffset(directions.get(x, y), offset);
			System.out.println( "Offset is " + offset[0] + " " + offset[1] );
			if( offset[0] > 5 )
			{
				return new Point2DInteger(x, y);
			}
			int x1 = x + offset[0];
			int y1 = y + offset[1];
			if( Float.isNaN(accumulation.get(x1, y1)) )
			{
				break;
			}
			float newAccumulation = accumulation.get(x1, y1);
			if( newAccumulation > threshold )
			{
				x = x1;
				y = y1;
				break;
			}
			if( newAccumulation <= currentAccumulation )
			{
				System.out.println(new RuntimeException("This is weird - FloatWorld.goDownstream"));
				break;
			}
			x = x1;
			y = y1;
		}
		return new Point2DInteger(x, y);
	}

	public void delineate(Point2DInteger outlet, ByteGridWritable watershed, ByteGrid directions)
	{

		Stack<Point2DInteger> nodes = new Stack<Point2DInteger>();
		nodes.add(outlet);
		int counter = 0;
		while( nodes.size() != 0 )
		{
			Point2DInteger n = nodes.pop();
			ArrayList<Point2DInteger> children = FlowDirectionUtils.getUpstreamNodes(n, directions);
			if( children.size() == 0 )
			{
				watershed.set(n.getX(), n.getY(), (byte) 1);
			}
			else
			{
				for (Point2DInteger p : children)
				{
					watershed.set(p.getX(), p.getY(), (byte) 1);
					nodes.add(p);
				}
			}
			counter++;
			if( (counter & 0xffff) == 0xffff )
			{
				System.out.println("Delineate " + counter);
			}
		}

	}
}
