package starhydro.utils;

import starhydro.data.interfaces.ByteGrid;
import starhydro.data.interfaces.FloatGrid;
import starhydro.data.interfaces.visitors.ByteGridVisitor;
import starhydro.data.interfaces.visitors.FloatGrid3by3Visitor;
import starhydro.data.interfaces.visitors.FloatGridStatistics;
import starhydro.data.interfaces.visitors.FloatGridVisitor;

public class Executor
{
	public static void compute(FloatGrid grid, Rectangle2DInteger rect, FloatGridVisitor visitor)
	{
		int x0 = rect.getX();
		int y0 = rect.getY();
		int x1 = rect.getX() + rect.getWidth();
		int y1 = rect.getY() + rect.getHeight();
		for (int y = y0; y < y1; y++)
		{
			for (int x = x0; x < x1; x++)
			{
				visitor.execute(grid.get(x, y), x, y);
			}
		}
	}

	public static void compute(FloatGrid grid, Rectangle2DInteger rect, FloatGridStatistics visitor)
	{
		int x0 = rect.getX();
		int y0 = rect.getY();
		int x1 = rect.getX() + rect.getWidth();
		int y1 = rect.getY() + rect.getHeight();
		for (int y = y0; y < y1; y++)
		{
			for (int x = x0; x < x1; x++)
			{
				visitor.execute(grid.get(x, y));
			}
		}
	}

	public static void compute(FloatGrid grid, Rectangle2DInteger rect, FloatGrid3by3Visitor visitor)
	{
		int x0 = rect.getX();
		int y0 = rect.getY();
		int x1 = rect.getX() + rect.getWidth();
		int y1 = rect.getY() + rect.getHeight();
		for (int y = y0; y < y1; y++)
		{
			for (int x = x0; x < x1; x++)
			{
				float value = grid.get(x, y);
				float[] ret = new float[8];
				ret[0] = grid.get(x + 1, y + 0);
				ret[1] = grid.get(x + 1, y + 1);
				ret[2] = grid.get(x + 0, y + 1);
				ret[3] = grid.get(x - 1, y + 1);
				ret[4] = grid.get(x - 1, y + 0);
				ret[5] = grid.get(x - 1, y - 1);
				ret[6] = grid.get(x + 0, y - 1);
				ret[7] = grid.get(x + 1, y - 1);
				visitor.execute(x, y, value, ret);
			}
		}
	}

	public static void compute(ByteGrid grid, Rectangle2DInteger rect, ByteGridVisitor visitor)
	{
		int x0 = rect.getX();
		int y0 = rect.getY();
		int x1 = rect.getX() + rect.getWidth();
		int y1 = rect.getY() + rect.getHeight();
		for (int y = y0; y < y1; y++)
		{
			for (int x = x0; x < x1; x++)
			{
				visitor.execute(grid.get(x, y), x, y);
			}
		}
	}

	// public static void compute(ByteGrid grid, Rectangle2DInteger rect, ByteGridStatistics visitor)
	// {
	// int x0 = rect.getX();
	// int y0 = rect.getY();
	// int x1 = rect.getX() + rect.getWidth();
	// int y1 = rect.getY() + rect.getHeight();
	// for (int y = y0; y < y1; y++)
	// {
	// for (int x = x0; x < x1; x++)
	// {
	// visitor.execute(grid.get(x, y));
	// }
	// }
	// }
	//
	// public static void compute(ByteGrid grid, Rectangle2DInteger rect, ByteGrid3by3Visitor visitor)
	// {
	// int x0 = rect.getX();
	// int y0 = rect.getY();
	// int x1 = rect.getX() + rect.getWidth();
	// int y1 = rect.getY() + rect.getHeight();
	// for (int y = y0; y < y1; y++)
	// {
	// for (int x = x0; x < x1; x++)
	// {
	// float value = grid.get(x, y);
	// float[] ret = new float[8];
	// ret[0] = grid.get(x + 1, y + 0);
	// ret[1] = grid.get(x + 1, y + 1);
	// ret[2] = grid.get(x + 0, y + 1);
	// ret[3] = grid.get(x - 1, y + 1);
	// ret[4] = grid.get(x - 1, y + 0);
	// ret[5] = grid.get(x - 1, y - 1);
	// ret[6] = grid.get(x + 0, y - 1);
	// ret[7] = grid.get(x + 1, y - 1);
	// visitor.execute(x, y, value, ret);
	// }
	// }
	// }

}
