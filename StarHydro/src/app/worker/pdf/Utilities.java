package app.worker.pdf;

import java.awt.Point;

import star.hydrology.data.interfaces.Grid;

public class Utilities
{

	public static void getDownstreamPoint(Point point, Grid grid)
	{
		byte value = (byte) grid.getElementAt(point.x, point.y);
		if ((value & (byte) 1) != 0)
		{
			point.x += 1;
			return;
		}
		else if ((value & (byte) 2) != 0)
		{
			point.x += 1;
			point.y += 1;
			return;
		}
		else if ((value & (byte) 4) != 0)
		{
			point.y += 1;
			return;
		}
		else if ((value & (byte) 8) != 0)
		{
			point.x -= 1;
			point.y += 1;
			return;
		}
		else if ((value & (byte) 16) != 0)
		{
			point.x -= 1;
			return;

		}
		else if ((value & (byte) 32) != 0)
		{
			point.x -= 1;
			point.y -= 1;
			return;
		}
		else if ((value & (byte) 64) != 0)
		{
			point.y -= 1;
			return;
		}
		else if ((value & (byte) -128) != 0)
		{
			point.x += 1;
			point.y -= 1;
			return;
		}

	}
}
