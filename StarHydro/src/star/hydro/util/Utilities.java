package star.hydro.util;

import java.awt.Point;

public class Utilities
{
	public static void getDownstreamOffset(byte direction, Point offset)
	{
		int x_offset = 0;
		int y_offset = 0;
		switch (direction)
		{
		case 1: // E
			x_offset = 1;
			y_offset = 0;
			break;
		case 2: // SE
			x_offset = 1;
			y_offset = 1;
			break;
		case 4: // S
			x_offset = 0;
			y_offset = 1;
			break;
		case 8: // SW
			x_offset = -1;
			y_offset = 1;
			break;
		case 16: // W
			x_offset = -1;
			y_offset = 0;
			break;
		case 32: // NW
			x_offset = -1;
			y_offset = -1;
			break;
		case 64: // N
			x_offset = 0;
			y_offset = -1;
			break;
		case -128: // NE
			x_offset = 1;
			y_offset = -1;
			break;
		default:
//			(new RuntimeException("Should be in range 1-255 and it is " + direction )).printStackTrace();
			break;
		}
		offset.x = x_offset;
		offset.y = y_offset;
	}

	public static float[] trimFloatArray(float[] src, int len)
	{
		if (src != null)
		{
			if (src.length > len)
			{
				float[] ret = new float[len];
				System.arraycopy(src, 0, ret, 0, len);
				return ret;
			}
			else
			{
				return src;
			}

		}
		else
		{
			return null;
		}
	}
	
	public static int[] trimIntArray(int[] src, int len)
	{
		if (src != null)
		{
			if (src.length > len)
			{
				int[] ret = new int[len];
				System.arraycopy(src, 0, ret, 0, len);
				return ret;
			}
			else
			{
				return src;
			}

		}
		else
		{
			return null;
		}
	}
}
