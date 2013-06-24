package starhydro.utils;

import java.util.ArrayList;

import starhydro.data.interfaces.ByteGrid;

public class FlowDirectionUtils
{
	public final static byte DIR_E = 1;
	public final static byte DIR_SE = 2;
	public final static byte DIR_S = 4;
	public final static byte DIR_SW = 8;
	public final static byte DIR_W = 16;
	public final static byte DIR_NW = 32;
	public final static byte DIR_N = 64;
	public final static byte DIR_NE = -128;

	public final static byte[] directions = {
	        DIR_E , DIR_SE , DIR_S , DIR_SW , //
	        DIR_W , DIR_NW , DIR_N , DIR_NE };

	public final static Point2DInteger[] offsetDirections = {
	        new Point2DInteger(1, 0) , new Point2DInteger(1, 1) , new Point2DInteger(0, 1) , new Point2DInteger(-1, 1) , new Point2DInteger(-1, 0) , new Point2DInteger(-1, -1) , new Point2DInteger(0, -1) , new Point2DInteger(1, -1) };

	public static Point2DInteger getDownstreamOffset(byte value)
	{
		int offsetX = 0;
		int offsetY = 0;
		switch( value )
		{
		case DIR_E:
			offsetX = 1;
			offsetY = 0;
			break;
		case DIR_SE:
			offsetX = 1;
			offsetY = 1;
			break;
		case DIR_S:
			offsetX = 0;
			offsetY = 1;
			break;
		case DIR_SW:
			offsetX = -1;
			offsetY = 1;
			break;
		case DIR_W:
			offsetX = -1;
			offsetY = 0;
			break;
		case DIR_NW:
			offsetX = -1;
			offsetY = -1;
			break;
		case DIR_N:
			offsetX = 0;
			offsetY = -1;
			break;
		case DIR_NE:
			offsetX = 1;
			offsetY = -1;
			break;
		default:
		}
		return new Point2DInteger(offsetX, offsetY);
	}

	public static void getDownstreamOffset(byte value, int[] offset)
	{
		int offsetX = 0;
		int offsetY = 0;
		switch( value )
		{
		case DIR_E:
			offsetX = 1;
			offsetY = 0;
			break;
		case DIR_SE:
			offsetX = 1;
			offsetY = 1;
			break;
		case DIR_S:
			offsetX = 0;
			offsetY = 1;
			break;
		case DIR_SW:
			offsetX = -1;
			offsetY = 1;
			break;
		case DIR_W:
			offsetX = -1;
			offsetY = 0;
			break;
		case DIR_NW:
			offsetX = -1;
			offsetY = -1;
			break;
		case DIR_N:
			offsetX = 0;
			offsetY = -1;
			break;
		case DIR_NE:
			offsetX = 1;
			offsetY = -1;
			break;
		default:
			offsetX = Integer.MAX_VALUE;
			offsetY = Integer.MAX_VALUE;
		}
		offset[0] = offsetX;
		offset[1] = offsetY;
	}

	public static ArrayList<Point2DInteger> getUpstreamNodes(Point2DInteger point, ByteGrid flowDirection)
	{
		ArrayList<Point2DInteger> ret = new ArrayList<Point2DInteger>();
		for (int i = 0; i < 8; i++)
		{
			Point2DInteger offset = offsetDirections[i];
			byte expected = directions[i];
			byte direction = flowDirection.get(point.getX() - offset.getX(), point.getY() - offset.getY());
			if( expected == direction )
			{
				ret.add(new Point2DInteger(point.getX() - offset.getX(), point.getY() - offset.getY()));
			}
		}
		return ret;
	}
}
