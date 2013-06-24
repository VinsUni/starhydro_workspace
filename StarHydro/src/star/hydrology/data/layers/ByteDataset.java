package star.hydrology.data.layers;

import java.awt.Color;

import star.hydrology.data.interfaces.GridRW;
import star.hydrology.ui.palette.Palette;

public class ByteDataset extends AbstractDataset implements GridRW
{
	private static final long serialVersionUID = 1L;
	private byte[] data;

	public float get(int x, int y)
	{
		return data[x + y * getCols()];
	}

	public byte getByte(int x, int y)
	{
		return data[x + y * getCols()];
	}

	public void set(int x, int y, byte value)
	{
		data[x + y * getCols()] = value;
	}

	public void setSize(int cols, int rows)
	{
		super.setSize(cols, rows);
		this.data = new byte[cols * rows];
	}

	public void setElementAt(int x, int y, float value)
	{
		set(x, y, (byte) value);
	}

	public Color getColorAt(int x, int y, Palette p)
	{
		return p.getColor(get(x, y) / 255);
	}

	public float getElementAt(int x, int y)
	{
		return get(x, y);
	}

	public void getPoint(int x, int y, float[] array)
	{
		array[0] = (float) (getCenter().x + (x - getCols() / 2) * getCellsize());
		array[1] = (float) (getCenter().y - (y - getCols() / 2) * getCellsize());
		array[2] = getElementAt(x, y);
	}

	private String dump()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("size=" + getRows() + "," + getCols() + "\n");
		sb.append("cellsize=" + getCellsize() + "\n");
		sb.append("center=" + getCenter() + "\n");
		int rows = getRows();
		int cols = getCols();
		for (int y = 0; y < rows; y++)
		{
			for (int x = 0; x < cols; x++)
			{
				sb.append(Math.ceil(100 * getElementAt(x, y)) / 100 + "\t");
			}
			sb.append("\n");
		}

		return sb.toString();
	}

}
