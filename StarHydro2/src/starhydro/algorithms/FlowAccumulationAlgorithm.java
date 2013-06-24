package starhydro.algorithms;

import java.util.ArrayList;
import java.util.Stack;

import starhydro.data.interfaces.ByteGrid;
import starhydro.data.interfaces.FloatGrid;
import starhydro.data.interfaces.FloatGridWritable;
import starhydro.utils.FlowDirectionUtils;
import starhydro.utils.Point2DInteger;
import starhydro.utils.Point3DDouble;
import starhydro.utils.Rectangle2DInteger;

public class FlowAccumulationAlgorithm
{
	ByteGrid flowDirection;
	FloatGridWritable flowAccumulation;
	FloatGrid elev;
	ArrayList<Rectangle2DInteger> range;
	int elementsPerDegree ;
	Stack<Point2DInteger> nodes = new Stack<Point2DInteger>();

	public void calculate(ArrayList<Rectangle2DInteger> range, FloatGridWritable elevation, ByteGrid flowDirection, FloatGridWritable flowAccumulation, int elementsPerDegree)
	{
		this.elementsPerDegree = elementsPerDegree ;
		this.flowDirection = flowDirection;
		this.flowAccumulation = flowAccumulation;
		this.elev = elevation;
		this.range = range;
		clearFlowAccumulation();
		initalizeNodes();
		loop();
	}

	private void clearFlowAccumulation()
	{
		for (Rectangle2DInteger rect : this.range)
		{
			int x0 = rect.getX();
			int y0 = rect.getY();
			int w = rect.getWidth();
			int h = rect.getHeight();
			int x1 = x0 + w;
			int y1 = y0 + h;
			for (int y = y0; y < y1; y++)
			{
				for (int x = x0; x < x1; x++)
				{
					flowAccumulation.set(x, y, Float.NaN);
				}
			}
		}
	}

	private void initalizeNodes()
	{
		nodes.clear();
		for (Rectangle2DInteger rect : this.range)
		{
			int x0 = rect.getX();
			int y0 = rect.getY();
			int x1 = x0 + rect.getWidth();
			int y1 = y0 + rect.getHeight();
			for (int y = y0; y < y1; y++)
			{
				for (int x = x0; x < x1; x++)
				{
					float z = elev.get(x, y);
					if( !Float.isNaN(z) )
					{
						float[] h = elev.get3x3(x, y);
						for (float height : h)
						{
							if( Float.isNaN(height) )
							{
								nodes.push(new Point2DInteger(x, y));
								break;
							}
						}
					}
				}
			}
		}
		System.out.println("Total nodes " + nodes.size());
	}

	WGS84Distances conv = new WGS84Distances();
	private float getArea( Point2DInteger area )
	{
		float longitude1 = 1.0f*area.getX()/elementsPerDegree;
		float latitude1 = 90-1.0f*area.getY()/elementsPerDegree;
		float longitude2 = 1.0f*(area.getX()+1)/elementsPerDegree;
		float latitude2 = 90-1.0f*(area.getY()+1)/elementsPerDegree;
		
		
		Point3DDouble dist11 = conv.positionDeg(latitude1, longitude1, 0);
		Point3DDouble dist12 = conv.positionDeg(latitude1, longitude2, 0);
		Point3DDouble dist21 = conv.positionDeg(latitude2, longitude1, 0);
		double distX = dist11.distance(dist12);
		double distY = dist11.distance(dist21);
		return ( float ) ( distX * distY ) ;
		
	}
	private void loop()
	{
		float max = 0;
		int counter = 0;
		while( nodes.size() != 0 )
		{
			Point2DInteger n = nodes.peek();
			ArrayList<Point2DInteger> children = FlowDirectionUtils.getUpstreamNodes(n, flowDirection);
			if( children.size() == 0 )
			{
				flowAccumulation.set(n.getX(), n.getY(), 0);
				nodes.pop();
			}
			else
			{
				float sum = 0;
				boolean hasUndefined = false;
				for (Point2DInteger p : children)
				{
					float acc = flowAccumulation.get(p.getX(), p.getY());
					if( Float.isNaN(acc) )
					{
						hasUndefined = true;
						nodes.add(p);
					}
					else
					{
						sum += acc;
						sum += getArea( p ) ;
					}
				}
				if( !hasUndefined )
				{
					max = Math.max(max, sum);
					flowAccumulation.set(n.getX(), n.getY(), sum);
					nodes.pop();
				}
			}
			counter++;
			if( (counter & 0xffff) == 0xffff )
			{
				System.out.println("FlowAcc " + counter + " " + max);
			}
		}
		System.out.println("Done");
	}
}
