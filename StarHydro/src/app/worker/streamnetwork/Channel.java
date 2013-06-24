package app.worker.streamnetwork;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import star.hydrology.events.interfaces.PaletteRenderableLayer;

public class Channel
{
	private final int INVALID = -1;

	private ArrayList<Point> points = new ArrayList<Point>(32);
	private ArrayList<Channel> children = new ArrayList<Channel>(0);
	private Channel parent;
	private PaletteRenderableLayer terrain;
	private int channelOrder = INVALID;
	private float length = INVALID;
	private PaletteRenderableLayer flowAcc;

	public Channel(PaletteRenderableLayer terrain, PaletteRenderableLayer flowAcc)
	{
		setTerrain(terrain);
		setFlowAccomulation(flowAcc);
	}

	public int getPointsSize()
	{
		return points.size();
	}

	public Point getPoint2D(int index)
	{
		return points.get(index);
	}

	public float[] getPoint(int index)
	{
		float[] f1 = new float[3];
		Point p1 = points.get(index);
		getTerrain().getDataset().getPoint(p1.x, p1.y, f1);
		return f1;
	}

	public void addPoint(int x, int y)
	{
		points.add(new Point(x, y));
		length = INVALID;
	}

	public void addChild(final Channel child)
	{
		points.trimToSize();
		child.setParent(this);
		children.add(child);
		channelOrder = INVALID;
		if (parent != null)
		{
			parent.points.trimToSize();
			parent.channelOrder = INVALID;
		}
	}

	public void setParent(final Channel parent)
	{
		this.parent = parent;
	}

	public ArrayList<Channel> getChildren()
	{
		return children;
	}

	public String toString()
	{
		if (children.size() == 0)
		{
			return "[" + getChannelOrder() + "]";
		}
		else
		{
			return "[" + getChannelOrder() + "," + children + "]";
		}
	}

	private void clearChannelOrder()
	{
		channelOrder = INVALID;
	}

	public int getChannelOrder()
	{
		if (channelOrder == INVALID)
		{
			if (getChildren().size() == 0)
			{
				channelOrder = 1;
			}
			else
			{
				int max = 0;
				int maxCount = 0;
				Iterator<Channel> iter = getChildren().iterator();
				while (iter.hasNext())
				{
					Channel child = iter.next();
					int order = child.getChannelOrder();
					if (order > max)
					{
						max = order;
						maxCount = 1;
					}
					else if (order == max)
					{
						maxCount++;
					}
				}
				if (maxCount > 1)
				{
					channelOrder = max + 1;
				}
				else
				{
					channelOrder = max;
				}
			}
		}
		return channelOrder;
	}

	public float getStraightLineLength()
	{
		float[] f1 = new float[3];
		float[] f2 = new float[3];
		Point p1 = points.get(0);
		Point p2 = points.get(points.size() - 1);
		getTerrain().getDataset().getPoint(p1.x, p1.y, f1);
		getTerrain().getDataset().getPoint(p2.x, p2.y, f2);
		float d = 0;
		for (int i = 0; i < 3; i++)
		{
			float dd = f1[i] - f2[i];
			d += dd * dd;
		}
		return (float) Math.sqrt(d);

	}

	public float getLength()
	{
		if (length == INVALID)
		{
			length = 0;
			float[] f1 = new float[3];
			float[] f2 = new float[3];
			Point p1 = points.get(0);
			Iterator<Point> iter = points.iterator();
			while (iter.hasNext())
			{

				Point p2 = iter.next();
				getTerrain().getDataset().getPoint(p1.x, p1.y, f1);
				getTerrain().getDataset().getPoint(p2.x, p2.y, f2);
				p1 = p2;
				float d = 0;
				for (int i = 0; i < 3; i++)
				{
					float dd = f1[i] - f2[i];
					d += dd * dd;
				}

				length += (float) Math.sqrt(d);
			}
		}
		return length;
	}

	public float getArea()
	{
		Point p1 = points.get(0);
		float cellsize = getFlowAccomulation().getDataset().getCellsize();
		return cellsize * cellsize * getFlowAccomulation().getDataset().getElementAt(p1.x, p1.y);
	}

	private void setTerrain(PaletteRenderableLayer terrain)
	{
		this.terrain = terrain;
	}

	private void setFlowAccomulation(PaletteRenderableLayer flowAcc)
	{
		this.flowAcc = flowAcc;
	}

	private PaletteRenderableLayer getTerrain()
	{
		return terrain;
	}

	private PaletteRenderableLayer getFlowAccomulation()
	{
		return flowAcc;
	}

}
