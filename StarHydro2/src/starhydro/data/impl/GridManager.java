package starhydro.data.impl;

import java.text.MessageFormat;
import java.util.Collection;

import starhydro.data.interfaces.Factory;
import starhydro.utils.Point2DInteger;
import starhydro.utils.Rectangle2DInteger;

public class GridManager<T>
{
	protected final T elements[][];
	private final int elementsPerGrid;
	private final String name;
	private final Rectangle2DInteger rect;

	public GridManager(final T[][] elements, final int elementsPerGrid, final String name)
	{
		this.elements = elements;
		this.elementsPerGrid = elementsPerGrid;
		this.name = name;
		int xmax = elements.length;
		int ymax = elements[0].length;
		rect = new Rectangle2DInteger(0, 0, xmax, ymax);
	}

	public int getElementsPerGrid()
	{
		return elementsPerGrid ;
	}
	public void clearTiles( Collection<Point2DInteger> points , Factory<T> factory )
	{
		for( Point2DInteger i : points )
		{
			elements[i.getX()][i.getY()] = factory.get();
		}
	}
	public void setTile(final int x, final int y, final T tile)
	{
		elements[x % rect.getWidth()][y] = tile;
	}

	public T getTile(final int x, final int y)
	{
		return elements[x % rect.getWidth()][y];
	}

	public boolean hasTile(final int x, final int y)
	{
		if( rect.contains(x, y) )
		{
			return elements[x][y] != null;
		}
		else
		{
			return false;
		}
	}
	
	public Rectangle2DInteger getRectangle( final int x , final int y )
	{
		return new Rectangle2DInteger(x*elementsPerGrid,y*elementsPerGrid,elementsPerGrid,elementsPerGrid);
	}

	public Coord4D getCoordinates(final int x, final int y)
	{
		Coord4D c = new Coord4D();
		c.x = x % elementsPerGrid;
		c.y = y % elementsPerGrid;
		c.xx = x / elementsPerGrid;
		c.yy = y / elementsPerGrid;
		return c;
	}
	
	@Override
	public String toString()
	{
	    return MessageFormat.format( "[{0} name={1} elemRect={2}]", this.getClass().getName() , name , rect );
	}

}
