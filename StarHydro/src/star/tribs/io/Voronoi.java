package star.tribs.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Voronoi
{
	public static class Point2D
	{
		public float x;
		public float y;

		public Point2D(float x, float y)
		{
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString()
		{
			return "{" + x + "," + y + "}";
		}
	}

	public static class Polygon
	{
		int id;
		ArrayList<Point2D> points = new ArrayList<Point2D>();
		Point2D center;

		public Polygon(int id, float x, float y)
		{
			this.id = id;
			center = new Point2D(x, y);
		}

		void add(float x, float y)
		{
			points.add(new Point2D(x, y));
		}

		Iterable<Point2D> getPoints()
		{
			return points;
		}

		@Override
		public String toString()
		{

			return "[Polygon: " + id + "," + points.toString() + "]";
		}
	}

	public static ArrayList<Polygon> read(InputStream is) throws IOException
	{
		ArrayList<Polygon> ret = new ArrayList<Polygon>();
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		String line;
		Polygon polygon = null;
		int linenr = 0;
		while ((line = r.readLine()) != null)
		{
			linenr++;
			if ("END".equals(line))
			{
				if (polygon != null)
				{
					ret.add(polygon);
					polygon = null;
				}
			}
			else if (polygon == null)
			{
				String[] split = line.split(",");
				if (split.length != 3)
				{
					throw new RuntimeException("Invalid format " + linenr + ": " + line);
				}

				polygon = new Polygon(Integer.parseInt(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]));
			}
			else
			{
				String[] split = line.split(",");
				if (split.length != 2)
				{
					throw new RuntimeException("Invalid format " + linenr + ": " + line);
				}
				polygon.add(Float.parseFloat(split[0]), Float.parseFloat(split[1]));
			}
		}
		return ret;
	}

	public static class Range
	{
		public Point2D min;
		public Point2D max;

		@Override
		public String toString()
		{
			return "[" + min + "--" + max + "]";
		}

		public String size()
		{
			return "[" + (max.x - min.x) + " -- " + (max.y - min.y) + "]";
		}

		public java.awt.Dimension getSize()
		{
			return new java.awt.Dimension((int) (max.x - min.x), (int) (max.y - min.y));
		}
	}

	public static Range getRange(ArrayList<Polygon> data)
	{
		Point2D max = new Point2D(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
		Point2D min = new Point2D(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
		for (Polygon p : data)
		{
			for (Point2D point : p.getPoints())
			{
				max.x = Math.max(point.x, max.x);
				max.y = Math.max(point.y, max.y);
				min.x = Math.min(point.x, min.x);
				min.y = Math.min(point.y, min.y);
			}
		}
		Range ret = new Range();
		ret.max = max;
		ret.min = min;
		return ret;
	}

}
