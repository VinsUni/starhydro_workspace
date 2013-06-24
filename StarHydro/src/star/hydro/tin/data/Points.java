package star.hydro.tin.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;

class Point
{
	Point(Point p)
	{
		this.x = p.x;
		this.y = p.y;
		this.z = p.z;
		this.flag = p.flag;
	}

	Point(double x, double y, double z, int flag)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.flag = flag;
	}

	Point(String[] str)
	{
		x = Double.parseDouble(str[0]);
		y = Double.parseDouble(str[1]);
		z = Double.parseDouble(str[2]);
		flag = Integer.parseInt(str[3]);
	}

	double x, y, z;
	int flag;

	@Override
	public String toString()
	{
		return MessageFormat.format("{0} ,{1} ,{2} ,{3}", x, y, z, flag);
	}
}

public class Points extends ArrayList<Point>
{
	Point min = null;
	Point max = null;

	public boolean add(Point p)
	{
		if (min != null)
		{
			min.x = Math.min(p.x, min.x);
			min.y = Math.min(p.y, min.y);
			min.z = Math.min(p.z, min.z);

			max.x = Math.max(p.x, max.x);
			max.y = Math.max(p.y, max.y);
			max.z = Math.max(p.z, max.z);

		}
		else
		{
			min = new Point(p);
			max = new Point(p);
		}
		return super.add(p);

	}

	public static Points read(InputStream is) throws IOException
	{
		Points ret = new Points();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String numberOfLines = reader.readLine();
		if (numberOfLines != null)
		{
			String line;
			while ((line = reader.readLine()) != null)
			{
				String data[] = line.split("\\s+");
				if (data.length == 4)
				{
					ret.add(new Point(data));
				}
				else
				{
					System.err.println("Failed on " + line + " -- " + data.length);
				}
			}
		}
		return ret;
	}

	public static void main(String[] args)
	{
		try
		{
			java.io.FileInputStream fis = new java.io.FileInputStream("Z:/downloads/starHydroBasins/walnutgulch/Input/wg.points");
			Points p = Points.read(fis);
			System.out.println(p.size());
			System.out.println(p.min);
			System.out.println(p.max);
			System.out.println(".x " + (p.max.x - p.min.x));
			System.out.println(".y " + (p.max.y - p.min.y));
			System.out.println(".z " + (p.max.z - p.min.z));

		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}
}
