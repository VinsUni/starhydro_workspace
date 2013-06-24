package starhydro.data.impl;

import starhydro.data.interfaces.ByteGridWritable;

public class ByteGridManager extends GridManager<ByteGridWritable> implements ByteGridWritable
{

	public ByteGridManager(String name, int elementsPerGrid)
	{
		super(new ByteGridWritable[360][180], elementsPerGrid, name);
	}

	public void set(int x, int y, byte value)
	{
		Coord4D c = getCoordinates(x, y);
		if( hasTile(c.xx, c.yy) )
		{
			elements[c.xx][c.yy].set(c.x, c.y, value);
		}
	}

	public byte get(int x, int y)
	{
		Coord4D c = getCoordinates(x, y);
		ByteGridWritable tile = elements[c.xx][c.yy];
		return tile != null ? tile.get(c.x, c.y) : 0;
	}

}
