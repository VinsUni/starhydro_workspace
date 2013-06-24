package starhydro.utils;

import java.text.MessageFormat;
import java.util.Iterator;

public class Rectangle2DInteger
{
	private int x;
	private int y;
	private int width;
	private int height;

	public Rectangle2DInteger(int x, int y, int w, int h)
	{
		setX(x);
		setY(y);
		setWidth(w);
		setHeight(h);
	}

	public Rectangle2DInteger(float x, float y, float w, float h)
	{
		setX(Math.round(x));
		setY(Math.round(y));
		setWidth(Math.round(w));
		setHeight(Math.round(h));
	}

	public int getHeight()
	{
		return height;
	}

	private void setHeight(int height)
	{
		this.height = height;
	}

	public int getWidth()
	{
		return width;
	}

	private void setWidth(int width)
	{
		this.width = width;
	}

	public int getX()
	{
		return x;
	}

	private void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	private void setY(int y)
	{
		this.y = y;
	}

	public int getArea()
	{
		return getWidth() * getHeight();
	}

	@Override
	public boolean equals(Object obj)
	{
		if( obj instanceof Rectangle2DInteger )
		{
			Rectangle2DInteger that = (Rectangle2DInteger) obj;
			return this.getX() == that.getX() && this.getY() == that.getY() && this.getWidth() == that.getWidth() && this.getHeight() == that.getHeight();
		}
		else
		{
			return false;
		}
	}

	@Override
	public String toString()
	{
		return MessageFormat.format("[IntRectange2D x={0},y={1},w={2},h={3}]", x, y, width, height);
	}

	public boolean contains(int x, int y)
	{
		return(x >= getX() && x < (getX() + getWidth()) && y >= getY() && y < (getY() + getHeight()));
	}

	public boolean borders(int x, int y)
	{
		return (x == getX() && y >= getY() && y < getY() + getHeight()) //
		        || (x == (getX() + getWidth() - 1) && y >= getY() && y < getY() + getHeight()) //
		        || (y == getY() && x >= getX() && x < getX() + getWidth()) //
		        || (y == getY() + getHeight() - 1 && x >= getX() && x < getX() + getWidth());
	}

	public Rectangle2DInteger intersect(Rectangle2DInteger rect)
	{
		int x0 = Math.max(this.getX(), rect.getX());
		int y0 = Math.max(this.getY(), rect.getY());
		int x1 = Math.min(this.getX() + this.getWidth(), rect.getX() + rect.getWidth());
		int y1 = Math.min(this.getY() + this.getHeight(), rect.getY() + rect.getHeight());
		return new Rectangle2DInteger(x0, y0, x1 - x0, y1 - y0);
	}

	// TODO: test & apply
	private Iterator<Point2DInteger> getIterator(final Point2DInteger self)
	{
		return new Iterator<Point2DInteger>()
		{
			private int x0 = getX();
			private int y0 = getY();
			private int x1 = getX() + getWidth();
			private int y1 = getY() + getHeight();

			private int x = x0;
			private int y = y0;

			private boolean hasNext = true;
			private Point2DInteger point = self;

			public boolean hasNext()
			{
				return hasNext;
			}

			public Point2DInteger next()
			{
				x++;
				if( x == x1 )
				{
					y++;
					x = x0;
					if( y == y1 )
					{
						hasNext = false;
					}
				}
				return point;
			}

			public void remove()
			{
			}

		};
	}
}