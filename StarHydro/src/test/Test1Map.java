package test;

import java.io.FileWriter;
import java.io.IOException;

import javax.vecmath.Point3f;

import star.hydrology.data.layers.FloatDataset;

public class Test1Map
{
	void elevateEdges(FloatDataset set, int width, float amount)
	{
		int rows = set.getRows();
		int cols = set.getCols();
		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < width; j++)
			{
				float left = set.getElementAt(j, i);
				set.setElementAt(j, i, left + amount);
				float right = set.getElementAt(cols - j - 1, i);
				set.setElementAt(cols - j - 1, i, right + amount);
			}
		}

		for (int i = 0; i < cols; i++)
		{
			for (int j = 0; j < width; j++)
			{
				float top = set.getElementAt(i, j);
				set.setElementAt(i, j, top + amount);
				float bottom = set.getElementAt(i, rows - j - 1);
				set.setElementAt(i, rows - j - 1, bottom + amount);
			}
		}
	}

	void elevateY(FloatDataset set, float amount)
	{
		int rows = set.getRows();
		int cols = set.getCols();
		float norm_amount = amount / Math.max(rows, cols);
		for (int y = 0; y < rows; y++)
		{
			for (int x = 0; x < cols; x++)
			{
				int xoffset = (x < cols / 2) ? (cols / 2 - x) : (x - cols / 2);
				int yoffset = (y < rows / 2) ? (rows / 2 - y) : 0;
				int offset;
				if (xoffset < y && y < rows / 2)
				{
					offset = yoffset;
				}
				else
				{
					offset = xoffset + yoffset;
				}
				float value = set.getElementAt(x, y);
				set.setElementAt(x, y, value + offset * norm_amount);
			}
		}

	}

	void addBall(FloatDataset set, float amount)
	{
		int rows = set.getRows();
		int cols = set.getCols();
		int centerX = rows / 2;
		int centerY = cols;
		int radius = Math.min(rows, cols) / 2;
		for (int y = 0; y < centerY; y++)
		{
			float yy = Math.abs(centerY - y);
			for (int x = centerX - radius; x < centerX + radius; x++)
			{
				float value = set.getElementAt(x, y);
				float offset = (float) Math.sqrt(sqr(radius) - sqr(x - centerX) - sqr(yy / 2)) / radius;
				if (!Float.isNaN(offset))
				{
					set.setElementAt(x, y, value - offset * amount);
				}
				else
				{
					set.setElementAt(x, y, value - amount);
				}
			}
		}
	}

	float sqr(float a)
	{
		return a * a;
	}

	void reset(FloatDataset set, float amount)
	{
		int rows = set.getRows();
		int cols = set.getCols();
		for (int y = 0; y < rows; y++)
		{
			for (int x = 0; x < cols; x++)
			{
				set.setElementAt(x, y, amount);
			}
		}
	}

	void addYSlope(FloatDataset set, float amount)
	{
		int rows = set.getRows();
		int cols = set.getCols();
		for (int y = 0; y < rows; y++)
		{
			for (int x = 0; x < cols; x++)
			{
				set.setElementAt(x, y, set.getElementAt(x, y) - amount / rows * y);
			}
		}
	}

	void addXSlope(FloatDataset set, float amount)
	{
		int rows = set.getRows();
		int cols = set.getCols();
		for (int y = 0; y < rows; y++)
		{
			for (int x = 0; x < cols; x++)
			{
				set.setElementAt(x, y, set.getElementAt(x, y) - amount / cols * x);
			}
		}
	}

	void addRandom(FloatDataset set, float amount)
	{
		int rows = set.getRows();
		int cols = set.getCols();
		for (int y = 0; y < rows; y++)
		{
			for (int x = 0; x < cols; x++)
			{
				set.setElementAt(x, y, (float) (set.getElementAt(x, y) + Math.random() * amount * ((y < rows * 3 / 4 ? 100 : 10) + Math.pow(Math.abs(x * 1.0f - rows / 2) / rows, 2) * 80)));
			}
		}
	}

	void write(FloatDataset set, String filename)
	{
		FileWriter w;
		try
		{
			w = new FileWriter(filename);
			w.write("ncols " + set.getCols() + "\n");
			w.write("nrows " + set.getRows() + "\n");
			w.write("xllcorner " + 858648.375 + "\n");
			w.write("yllcorner " + 4874907 + "\n");
			w.write("cellsize " + set.getCellsize() + "\n");
			w.write("NODATA_value -9999\n");
			int rows = set.getRows();
			int cols = set.getCols();
			for (int y = 0; y < rows; y++)
			{
				for (int x = 0; x < cols; x++)
				{
					w.write(set.getElementAt(x, y) + " ");
				}
				w.write("\n");
			}
			w.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	int[] offsetX = new int[] { 1, 1, 0, -1, -1, -1, 0, 1 };
	int[] offsetY = new int[] { 0, 1, 1, 1, 0, -1, -1, -1 };
	int[] offsetDir = new int[] { 16, 32, 64, 128, 1, 2, 4, 8 };

	void walk(int x, int y, FloatDataset flowDir, FloatDataset flowAcc)
	{
		int childrenAccumulation = 0;
		for (int i = 0; i < offsetX.length; i++)
		{
			if (offsetDir[i] == (int) flowDir.getElementAt(x + offsetX[i], y + offsetY[i]))
			{
				walk(x + offsetX[i], y + offsetY[i], flowDir, flowAcc);
				childrenAccumulation += flowAcc.getElementAt(x + offsetX[i], y + offsetY[i]);
			}
		}
		flowAcc.setElementAt(x, y, childrenAccumulation + 1);
	}

	FloatDataset flowAccumulation(FloatDataset flowDir)
	{
		try
		{
			FloatDataset ret = (FloatDataset) flowDir.getSameCoverage(FloatDataset.class);
			walk(flowDir.getCols() / 2, flowDir.getRows() - 1, flowDir, ret);
			return ret;
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	FloatDataset flowDirection(FloatDataset set)
	{
		try
		{
			FloatDataset ret = (FloatDataset) set.getSameCoverage(FloatDataset.class);
			int rows = set.getRows();
			int cols = set.getCols();
			for (int y = 1; y < rows - 1; y++)
			{
				for (int x = 1; x < cols - 1; x++)
				{
					float height = set.getElementAt(x, y);
					float dirheight = height;
					int dir = 0;
					int[] offsetX = new int[] { 1, 1, 0, -1, -1, -1, 0, 1 };
					int[] offsetY = new int[] { 0, 1, 1, 1, 0, -1, -1, -1 };
					int[] offsetDir = new int[] { 1, 2, 4, 8, 16, 32, 64, 128 };

					for (int i = 0; i < offsetX.length; i++)
					{
						float nextheight = set.getElementAt(x + offsetX[i], y + offsetY[i]);
						if (nextheight < dirheight)
						{
							dirheight = nextheight;
							dir = offsetDir[i];
						}
					}
					ret.setElementAt(x, y, dir);
				}
			}
			return ret;
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	public static void main(String[] str)
	{
		Test1Map app = new Test1Map();
		FloatDataset set = new FloatDataset();
		set.setSize(400, 400);
		set.setCenter(new Point3f(0, 0, 0));
		set.setCellsize(60);
		app.reset(set, 500);
		app.elevateEdges(set, 1, -10);
		app.elevateY(set, 20);
		app.addBall(set, 400);
		app.addYSlope(set, 100f);
		app.addXSlope(set, 1f);
		app.addRandom(set, .005f);
		FloatDataset flowDir = app.flowDirection(set);
		FloatDataset flowAcc = app.flowAccumulation(flowDir);
		app.write(set, "c:\\temp\\starhydro_test1\\filled.xyz");
		app.write(set, "c:\\temp\\starhydro_test1\\projected.xyz");
		app.write(flowDir, "c:\\temp\\starhydro_test1\\flowdir.xyz");
		app.write(flowAcc, "c:\\temp\\starhydro_test1\\flowacc.xyz");

	}
}
