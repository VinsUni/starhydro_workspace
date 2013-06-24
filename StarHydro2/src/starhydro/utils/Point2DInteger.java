package starhydro.utils;

import java.io.Serializable;
import java.text.MessageFormat;

import utils.MathHelpers;

public class Point2DInteger implements Serializable
{
	private static final long serialVersionUID = 1L;
	int x;
	int y;

	public Point2DInteger(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	@Override
	public int hashCode()
	{
		return (x & 0xffff) | ((y & 0xffff) << 16);
	}

	@Override
	public boolean equals(Object obj)
	{
		boolean ret;
		if( obj instanceof Point2DInteger )
		{
			Point2DInteger other = (Point2DInteger) obj;
			ret = this.x == other.x && this.y == other.y;
		}
		else
		{
			ret = false;
		}
		return ret;
	}

	public Point2DInteger getOffsettedValue(int x, int y)
	{
		return new Point2DInteger(this.x + x, this.y + y);
	}

	public Point2DInteger getOffsettedValue(Point2DInteger offset)
	{
		return new Point2DInteger(this.x + offset.x, this.y + offset.y);
	}

	@Override
	public String toString()
	{
		return MessageFormat.format("[Point2DInteger {0},{1}]", x, y);
	}

	public float distance(Point2DInteger other)
	{
		return MathHelpers.distance(this.x, this.y, other.x, other.y);
	}

	public boolean borders(Rectangle2DInteger range)
	{
		boolean ret = false;
		if( range.getX() == this.getX() )
		{
			boolean c1 = range.getY() <= this.getY();
			boolean c2 = range.getY() + range.getHeight() >= this.getY();
			ret = c1 && c2;
		}
		else if( (range.getX() + range.getWidth()) == this.getX() )
		{
			boolean c1 = range.getY() <= this.getY();
			boolean c2 = range.getY() + range.getHeight() >= this.getY();
			ret = c1 && c2;
		}
		else if( range.getY() == this.getY() )
		{
			boolean c1 = range.getX() <= this.getX();
			boolean c2 = range.getX() + range.getWidth() >= this.getX();
			ret = c1 && c2;
		}
		else if( range.getY() + range.getHeight() == this.getY() )
		{
			boolean c1 = range.getX() <= this.getX();
			boolean c2 = range.getX() + range.getWidth() >= this.getX();
			ret = c1 && c2;
		}
		return ret;
	}
}
