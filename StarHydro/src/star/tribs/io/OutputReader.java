package star.tribs.io;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import star.tribs.io.Voronoi.Polygon;
import edu.mit.star.flv.Capture;
import edu.mit.star.flv.CaptureFactory;

public class OutputReader
{
	final static String dirname = "Z:\\hydro\\R3\\Output\\voronoi\\";
	final static String filename = dirname + "wg_voi";

	final static String prefix = "wg.";

	static String getFilename(String prefix, int hour, String suffix)
	{
		return MessageFormat.format("{0}{1,number,0000}_00{2}", prefix, hour, suffix);
	}

	static class VoronoiPoints
	{
		float[][] data;
		float[] min;
		float[] max;

		public VoronoiPoints(int size, int columns)
		{
			data = new float[size][columns];
			min = new float[columns];
			max = new float[columns];
			Arrays.fill(min, Float.POSITIVE_INFINITY);
			Arrays.fill(max, Float.NEGATIVE_INFINITY);
		}

		void set(int point, int column, float value)
		{
			data[point][column] = value;
			max[column] = Math.max(max[column], value);
			min[column] = Math.min(min[column], value);
		}

		float get(int point, int column)
		{
			return data[point][column];
		}

		float getNormalized(int point, int column)
		{
			if (max[column] == min[column])
			{
				return 0;
			}
			float val = get(point, column);
			return ((val - min[column]) / (max[column] - min[column]));
		}

	}

	static void read(java.io.File file, VoronoiPoints vor, String suffix, int columns) throws IOException
	{
		java.io.FileInputStream fis = new java.io.FileInputStream(file);
		java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(fis));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			StringTokenizer tk = new StringTokenizer(line, ",");
			int id = Integer.parseInt(tk.nextToken());
			int col = 0;
			vor.set(id, col++, id);
			while (tk.hasMoreTokens())
			{
				float value = Float.parseFloat(tk.nextToken());
				vor.set(id, col++, value);
				if (col >= columns)
				{
					break;
				}
			}
		}
		fis.close();
	}

	static void read(int hour, VoronoiPoints vor, String suffix) throws IOException
	{
		java.io.FileInputStream fis = new java.io.FileInputStream(dirname + getFilename(prefix, hour, suffix));
		java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(fis));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			StringTokenizer tk = new StringTokenizer(line, ",");
			int id = Integer.parseInt(tk.nextToken());
			int col = 0;
			vor.set(id, col++, id);
			while (tk.hasMoreTokens())
			{
				float value = Float.parseFloat(tk.nextToken());
				vor.set(id, col++, value);
			}
		}
		fis.close();
	}

	static VoronoiPoints[] read() throws IOException
	{
		int hours = 30 * 24;
		int rows = 19443;
		int columns = 81;
		VoronoiPoints[] vor = new VoronoiPoints[hours];
		for (int i = 0; i < hours; i += 24)
		{
			System.out.println("reading " + i + " " + Runtime.getRuntime().freeMemory());
			vor[i] = new VoronoiPoints(rows, columns);
			read(i, vor[i], "d");
			System.gc();
		}
		return vor;
	}

	static void max(float[] target, float[] source)
	{
		for (int i = 0; i < target.length; i++)
		{
			target[i] = Math.max(target[i], source[i]);
		}
	}

	static void min(float[] target, float[] source)
	{
		for (int i = 0; i < target.length; i++)
		{
			target[i] = Math.min(target[i], source[i]);
		}
	}

	static void process(java.io.File voronoiFile, java.io.File[] dataFiles, java.io.File flvFile, int factor, int frameIncrement, int columns)
	{
		try
		{
			ArrayList<Polygon> polygons = Voronoi.read(new FileInputStream(voronoiFile));
			VoronoiCanvas canvas = new VoronoiCanvas(polygons, factor, null);
			float[] maxArray = new float[columns];
			float[] minArray = new float[columns];
			Arrays.fill(maxArray, Float.NEGATIVE_INFINITY);
			Arrays.fill(minArray, Float.POSITIVE_INFINITY);
			for (java.io.File f : dataFiles)
			{
				System.out.println("Reading (pass 1)" + f);
				VoronoiPoints points = new VoronoiPoints(polygons.size(), columns);
				read(f, points, "d", columns);
				max(maxArray, points.max);
				min(minArray, points.min);
			}
			Dimension d = canvas.getPreferredSize();
			d.height = (d.height | 0x0f) + 1;
			d.width = (d.width | 0x0f) + 1;
			Capture capture[] = new Capture[columns];
			for (int currCol = 0; currCol < columns; currCol++)
			{
				capture[currCol] = CaptureFactory.getCapturer(new java.io.FileOutputStream(flvFile.getAbsolutePath() + currCol + ".flv"), d);
			}
			int timestamp = 0;
			for (java.io.File f : dataFiles)
			{
				System.out.println("Reading (pass 2)" + f);
				VoronoiPoints points = new VoronoiPoints(polygons.size(), columns);
				read(f, points, "d", columns);
				points.max = maxArray;
				points.min = minArray;
				for (int currCol = 0; currCol < columns; currCol++)
				{
					System.out.println("Writing " + f + " column " + currCol);
					BufferedImage image = capture[currCol].newFrame();
					canvas.p(image.getGraphics(), f.getName(), currCol, points);
					capture[currCol].writeFrame(image, timestamp);
				}
				timestamp += frameIncrement;
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	static void generateAll(int from, int to)
	{
		try
		{

			int rows = 19443;
			int columns = 81;

			ArrayList<Polygon> polygons = Voronoi.read(new FileInputStream(filename));
			VoronoiCanvas canvas = new VoronoiCanvas(polygons, 15, null);
			float[] maxArray = new float[columns];
			float[] minArray = new float[columns];
			Arrays.fill(maxArray, Float.NEGATIVE_INFINITY);
			Arrays.fill(minArray, Float.POSITIVE_INFINITY);

			for (int i = from; i < to; i += 24)
			{
				System.out.println("Reading (pass 1)" + i + " " + from + " " + to);
				VoronoiPoints points = new VoronoiPoints(rows, columns);
				read(i, points, "d");
				max(maxArray, points.max);
				min(minArray, points.min);
			}
			for (int i = from; i < to; i += 24)
			{
				System.out.println("Reading (pass 2)" + i + " " + from + " " + to);
				VoronoiPoints points = new VoronoiPoints(rows, columns);
				read(i, points, "d");
				points.max = maxArray;
				points.min = minArray;
				BufferedImage image = new BufferedImage(canvas.getPreferredSize().width, canvas.getPreferredSize().height, BufferedImage.TYPE_BYTE_GRAY);
				for (int currCol = 0; currCol < 81; currCol++)
				{
					canvas.p(image.getGraphics(), i, currCol, points);
					java.io.File f = new java.io.File(MessageFormat.format("z:\\hydro\\R3\\Images\\{0}\\img_{0}_{1,number,0000}{2}", currCol, i, ".png"));
					if (!f.getParentFile().exists())
					{
						System.out.println("making " + f.getParentFile());
						f.getParentFile().mkdirs();
					}
					System.out.println("writing " + f);
					ImageIO.write(image, "PNG", f);
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public static void threadId(final int i)
	{
		Thread t = new Thread()
		{
			@Override
			public void run()
			{
				int from = 364 * 24 / 4 * i;
				int to = 364 * 24 / 4 * (i + 1);
				generateAll(from, to);
			}
		};
		t.start();
	}

	public static void main(String[] args)
	{
		for (int i = 0; i < 4; i++)
		{
			// threadId(i);
		}
		generateAll(0, 8640);
	}

	public static void main2(String[] args)
	{
		try
		{
			VoronoiPoints[] points = read();
			ArrayList<Polygon> polygons = Voronoi.read(new FileInputStream(filename));
			System.out.println(polygons.get(0));
			System.out.println(polygons.get(32));
			Voronoi.Range r = Voronoi.getRange(polygons);
			System.out.println(r);
			System.out.println(r.size());
			Frame f = new Frame();
			VoronoiCanvas canvas = new VoronoiCanvas(polygons, 15, points);
			Play p = new Play(canvas);
			f.setLayout(new BorderLayout());
			f.add(BorderLayout.CENTER, canvas);
			f.add(BorderLayout.NORTH, p);
			f.setVisible(true);
			f.pack();
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}
}
