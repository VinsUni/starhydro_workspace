package star.hydrology.data.layers;

import java.awt.Color;
import java.io.Serializable;

import star.hydrology.data.interfaces.GridRW;
import star.hydrology.data.interfaces.GridwStat;
import star.hydrology.ui.palette.Palette;

public class FloatDataset extends AbstractDataset implements GridRW, GridwStat, Serializable
{

	private static final long serialVersionUID = 1L;
	private float max = Float.MIN_VALUE;
	private float min = Float.MAX_VALUE;
	private float[] data;

	public void clear()
	{
		java.util.Arrays.fill(data, Float.NaN);
		max = Float.NaN;
		min = Float.NaN;
	}

	public float getMaximum()
	{
		return max;
	}

	public float getMinimum()
	{
		return min;
	}

	public void recalculateMinimumAndMaximum()
	{
		max = Float.MIN_VALUE;
		min = Float.MAX_VALUE;
		if (data != null)
		{
			for (int i = 0; i < data.length; i++)
			{
				float value = data[i];
				if (!Float.isNaN(value))
				{
					min = min > value ? value : min;
					max = max > value ? max : value;
				}
			}
		}
	}

	public Color getColorAt(int x, int y, Palette p)
	{
		return p.getColor((getElementAt(x, y) - min) / (max - min));
	}

	public float getElementAt(int x, int y)
	{
		int index = x + y * getCols();
		if (index < data.length && index > 0)
		{
			return data[x + y * getCols()];
		}
		else
		{
			return Float.NaN;
		}

	}

	public void setElementAt(int x, int y, float value)
	{
		data[x + y * getCols()] = value;
		if (!Float.isNaN(value))
		{
			min = min > value ? value : min;
			max = max > value ? max : value;
		}
	}

	public void setSize(int cols, int rows)
	{
		super.setSize(cols, rows);
		if (data != null)
		{
			allocated -= data.length;
		}
		this.data = new float[cols * rows];
		for (int i = 0; i < data.length; i++)
		{
			data[i] = Float.NaN;
		}
		if (data != null)
		{
			allocated += data.length;
		}
	}

	@Override
	protected void finalize() throws Throwable
	{
		if (data != null)
		{
			allocated -= data.length;
		}
		super.finalize();
	}

	private static int allocated = 0;

	public void getPoint(int x, int y, float[] array)
	{
		array[0] = (float) (getCenter().x + (x - getCols() / 2) * getCellsize());
		array[1] = (float) (getCenter().y - (y - getRows() / 2) * getCellsize());
		array[2] = getElementAt(x, y);
	}

	public String dumpAscii()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("ncols " + getCols() + "\n");
		sb.append("nrows " + getRows() + "\n");
		sb.append("xllcorner " + (getCenter().x - getCols() / 2 * getCellsize()) + "\n");
		sb.append("yllcorner " + (getCenter().y - getRows() / 2 * getCellsize()) + "\n");
		sb.append("cellsize " + getCellsize() + "\n");
		sb.append("NODATA_value -9999\n");
		int rows = getRows();
		int cols = getCols();
		for (int y = 0; y < rows; y++)
		{
			for (int x = 0; x < cols; x++)
			{

				if (!Float.isNaN(getElementAt(x, y)))
				{
					sb.append(Math.ceil(100 * getElementAt(x, y)) / 100 + "\t");
				}
				else
				{
					sb.append("-9999\t");
				}

			}
			sb.append("\n");
		}

		return sb.toString();
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
