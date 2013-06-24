package starhydro.algorithms;

import java.text.MessageFormat;

final class Node implements Comparable<Node>
{
	public Node(int x, int y, float spill)
	{
		this.x = x;
		this.y = y;
		this.spill = spill;
		if( Float.isNaN(spill) )
		{
			throw new RuntimeException();
		}
	}

	private int x;
	private int y;
	private float spill = 0;

	public final int getX()
	{
		return x;
	}

	public final int getY()
	{
		return y;
	}

	public final float getSpill()
	{
		return spill;
	}

	public final int compareTo(Node o)
	{
		int cmp = Float.compare(this.spill, o.spill);
		if( cmp == 0 )
		{
			if( o.hashCode() == this.hashCode() )
			{
				return 0;
			}
			if( o.hashCode() > this.hashCode() )
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}
		return cmp;
	}

	@Override
	public final String toString()
	{
		return MessageFormat.format("[Node {0} {1} {2}]", x, y, spill);
	}
}