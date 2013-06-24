package app.worker.streamnetwork;

import java.util.ArrayList;
import java.util.Iterator;

public class Stream
{
	final int INVALID = -1;
	private ArrayList<Channel> parts = new ArrayList<Channel>();
	private ArrayList<Stream> children = new ArrayList<Stream>();

	public Stream(Channel c)
	{
		generate(c);
	}

	public void addPart(Channel c)
	{
		parts.add(c);
	}

	public void addChild(Stream s)
	{
		children.add(s);
	}

	public ArrayList<Stream> getChildren()
	{
		return children;
	}

	public ArrayList<Channel> getParts()
	{
		return parts;
	}

	private void generate(Channel c)
	{
		addPart(c);
		int order = c.getChannelOrder();
		Iterator<Channel> iter = c.getChildren().iterator();
		while (iter.hasNext())
		{
			Channel child = iter.next();
			if (child.getChannelOrder() == order)
			{
				addPart(child);
				generate(child);
			}
			else
			{
				Stream s = new Stream(child);
				addChild(s);
			}
		}
	}

	public float getLength()
	{
		float length = 0;
		Iterator<Channel> iter = getParts().iterator();
		while (iter.hasNext())
		{
			Channel c = iter.next();
			length += c.getLength();
		}
		return length;
	}

	public float getArea()
	{
		return getParts().get(0).getArea();
	}

	public int getStreamOrder()
	{
		return getParts().get(0).getChannelOrder();
	}

	public float[] getOutletHeight()
	{
		return getParts().get(0).getPoint(0);
	}

	public float[] getPeakPoint()
	{
		Channel lastChannel = getParts().get(getParts().size() - 1);
		return lastChannel.getPoint(lastChannel.getPointsSize() - 1);
	}

	public String toString()
	{
		if (getChildren().size() != 0)
		{
			return "[" + getParts().get(0).getChannelOrder() + "," + getLength() + "," + getChildren() + "]";
		}
		else
		{
			return "[" + getLength() + "]";
		}
	}
}