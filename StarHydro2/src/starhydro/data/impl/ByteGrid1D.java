package starhydro.data.impl;

import starhydro.data.interfaces.ByteGridWritable;
import starhydro.data.interfaces.Factory;

public class ByteGrid1D implements ByteGridWritable
{
	private final int rows;
	private final int cols;
	private final byte[] data;

	public static Factory<ByteGridWritable> getFactory(final int x, final int y)
	{
		return new Factory<ByteGridWritable>()
		{
			public ByteGrid1D get()
			{
				return new ByteGrid1D(x, y);
			}
		};
	}
	public ByteGrid1D(int cols, int rows, byte[] data)
	{
		this.rows = rows;
		this.cols = cols;
		this.data = data;
	}

	public ByteGrid1D(int cols, int rows)
	{
		this.rows = rows;
		this.cols = cols;
		this.data = new byte[cols * rows];
	}

	public byte get(int x, int y)
	{
		return data[x + y * cols];
	}

	public void set(int x, int y, byte value)
	{
		data[x + y * cols] = value;
	}

	public int getRows()
	{
		return rows;
	}

	public int getCols()
	{
		return cols;
	}

}
