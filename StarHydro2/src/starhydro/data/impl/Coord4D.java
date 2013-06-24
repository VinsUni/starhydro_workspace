/**
 * 
 */
package starhydro.data.impl;

import java.text.MessageFormat;

public class Coord4D
{
	public int x, y;
	public int xx, yy;

	@Override
	public String toString()
	{
		return MessageFormat.format("[xx={0}/{2} yy={1}/{3}] ", xx, yy, x, y);
	}

	public String getCoords()
	{
		return MessageFormat.format("[long={0,number, ##0.000;-##0.000} lat={1,number, 00.000;-00.000}] ", (xx - 180)+x*1.0f/1200, (90 - yy)+y*1.0f/1200);
	}

}