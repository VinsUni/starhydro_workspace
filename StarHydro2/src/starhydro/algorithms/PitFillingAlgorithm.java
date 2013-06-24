package starhydro.algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Stack;

import starhydro.data.interfaces.FloatGridWritable;
import starhydro.utils.FlowDirectionUtils;
import starhydro.utils.Point2DInteger;
import starhydro.utils.Rectangle2DInteger;
import utils.ArrayNumerics;

public class PitFillingAlgorithm
{
	private FloatGridWritable elev;
	private FloatGridWritable filled;
	private ArrayList<Rectangle2DInteger> range;

	PriorityQueue<Node> nodes = new PriorityQueue<Node>();

	public void calculate(ArrayList<Rectangle2DInteger> range, FloatGridWritable elev, FloatGridWritable filled)
	{
		this.elev = elev;
		this.filled = filled;
		this.range = range;
		preprocess();
		fillNodes();
		iterate();
	}

	HashSet<Point2DInteger> getConnectedNaNs(int x, int y, int maxSize)
	{
		HashSet<Point2DInteger> ret = new HashSet<Point2DInteger>();
		Stack<Point2DInteger> stack = new Stack<Point2DInteger>();
		stack.push(new Point2DInteger(x, y));
		while( stack.size() != 0 )
		{
			Point2DInteger p = stack.pop();
			ret.add(p);
			for (Point2DInteger offset : FlowDirectionUtils.offsetDirections)
			{
				Point2DInteger testPoint = new Point2DInteger(p.getX() + offset.getX(), p.getY() + offset.getY());
				if( Float.isNaN(elev.get(testPoint.getX(), testPoint.getY())) && !ret.contains(testPoint) )
				{
					stack.push(testPoint);
					ret.add(testPoint);
					if( ret.size() > maxSize )
					{
						return null;
					}
				}
			}
		}
		return ret;
	}

	private void preprocess()
	{
		int fixSingle = 0;
		int belowSufrace = 0;
		for (Rectangle2DInteger rect : this.range)
		{
			int cols = rect.getWidth();
			int rows = rect.getHeight();
			int x0 = rect.getX();
			int y0 = rect.getY();
			int x1 = x0 + cols;
			int y1 = y0 + rows;
			for (int y = y0; y < y1; y++)
			{
				for (int x = x0; x < x1; x++)
				{
					float z = elev.get(x, y);
					if( z <= 0 )
					{
						elev.set(x, y, Float.NaN);
						belowSufrace++;
					}
				}
			}
			for (int y = y0; y < y1; y++)
			{
				for (int x = x0; x < x1; x++)
				{
					float z = elev.get(x, y);
					if( Float.isNaN(z) )
					{
						float[] h = elev.get3x3(x, y);
						if( !ArrayNumerics.containsNaN(h) )
						{
							elev.set(x, y, ArrayNumerics.average(h, false));
							fixSingle++;
						}
						else
						{
							HashSet<Point2DInteger> points = getConnectedNaNs(x, y, 10);
							if( points != null )
							{
								for (Point2DInteger p : points)
								{
									elev.set(p.getX(), p.getY(), 0);
								}
							}
						}
					}
				}
			}
		}
		System.out.println("Fix single NaN " + fixSingle + " below surface fixes " + belowSufrace);
	}

	private void fillNodes()
	{
		nodes.clear();
		for (Rectangle2DInteger rect : this.range)
		{
			int cols = rect.getWidth();
			int rows = rect.getHeight();
			int x0 = rect.getX();
			int y0 = rect.getY();
			int x1 = x0 + cols;
			int y1 = y0 + rows;
			for (int y = y0; y < y1; y++)
			{
				for (int x = x0; x < x1; x++)
				{
					float z = elev.get(x, y);
					if( !Float.isNaN(z) )
					{
						float[] h = elev.get3x3(x, y);
						if( ArrayNumerics.containsNaN(h) )
						{
							setFilledAndAddNodes(x, y, z);
						}
					}
				}
			}
		}
		System.out.println("Total nodes " + nodes.size());

	}

	private void setFilledAndAddNodes(int x, int y, float z)
	{
		Node n = new Node(x, y, z);
		nodes.add(n);
		filled.set(x, y, z);
		float f = filled.get(x, y);
		if( Float.isNaN(f) || f != z )
		{
			System.out.println("ERROR4 " + x + " " + y + " " + z + " " + n + " " + f);
			throw new RuntimeException();
		}
	}

	private void iterate()
	{
		float minDiff = 0.01f;
		float maxHeight = Float.MIN_VALUE;
		int progress = 0;
		int[] offset = new int[2];

		while( nodes.size() != 0 )
		{
			Node node = nodes.remove();
			int x = node.getX();
			int y = node.getY();
			float z = filled.get(x, y);
			if( Float.isNaN(z) || z != node.getSpill() )
			{
				System.out.println("ERROR1 " + x + " " + y + " " + z + " " + node);
				throw new RuntimeException();
			}
			for (byte b : FlowDirectionUtils.directions)
			{
				FlowDirectionUtils.getDownstreamOffset(b, offset);
				int x1 = x + offset[0];
				int y1 = y + offset[1];
				float iz = elev.get(x1, y1);
				if( Float.isNaN(filled.get(x1, y1)) && !Float.isNaN(iz) )
				{
					if( iz < z + minDiff )
					{
						iz = z + minDiff;
					}

					maxHeight = Math.max(maxHeight, iz);
					setFilledAndAddNodes(x1, y1, iz);
				}
			}

			progress++;
			if( (progress & 0x2ffff) == 0x2ffff )
			{
				System.out.println("Progress " + progress + " " + nodes.size() + " " + nodes.peek() + " " + maxHeight);
			}
		}

	}
}
