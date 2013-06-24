package utils;

import java.text.NumberFormat;

public class Format
{
	public static String formatNumber(float number)
	{
		if( Float.isNaN(number) )
		{
			return "";
		}
		return NumberFormat.getNumberInstance().format(number);
	}

	public static String formatNumber(double number)
	{
		return NumberFormat.getNumberInstance().format(number);
	}
}
