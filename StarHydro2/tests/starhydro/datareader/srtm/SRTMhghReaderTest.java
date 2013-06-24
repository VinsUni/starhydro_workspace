package starhydro.datareader.srtm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import starhydro.data.impl.FloatGrid1D;
import starhydro.data.interfaces.FloatGrid;
import starhydro.data.interfaces.visitors.FloatGrid3by3Visitor;
import starhydro.data.interfaces.visitors.FloatGridStatistics;
import starhydro.data.reader.srtm.LocalSRTMhgtSource;
import starhydro.data.reader.srtm.SRTMhgtReader;

public class SRTMhghReaderTest
{
	int width = 3601;
	int height = 3601;
	File file;
	SRTMhgtReader reader;

	@Before
	public void setupFile()
	{
		file = new File("test_data/N43W075.hgt");
		assertTrue(file.exists());

		long length = file.length();
		assertTrue(length == width * height * 2);

		reader = new SRTMhgtReader();
	}

	private static long time1dLoad = Long.MIN_VALUE;

	private static long time1dMin = Long.MIN_VALUE;
	private static long time1dMinDirect = Long.MIN_VALUE;

	private static long time1dMissing = Long.MIN_VALUE;
	private static long time1dMissingDirect = Long.MIN_VALUE;

	private static long time1dVisitorStats = Long.MIN_VALUE;
	private static long time1dVisitorDir = Long.MIN_VALUE;

	class FindMinimum implements FloatGridStatistics
	{
		float minimum = Float.NaN;

		public float result()
		{
			return minimum;
		}

		public void execute(float value)
		{
			if( !Float.isNaN(value) )
			{
				minimum = minimum > value ? value : minimum;
			}

		}

		public void done()
		{
		}

	}

	class FlowDir implements FloatGrid3by3Visitor
	{
		int[] dirs = new int[10];

		public void execute(int x, int y, float value, float[] data)
		{
			float min = Float.POSITIVE_INFINITY;
			int dir = 9;
			for (int i = 0; i < data.length; i++)
			{
				if( min > data[i] )
				{
					min = data[i];
					dir = i;
				}
			}
			dirs[dir]++;
		}
	}

	@Test
	public void timing1() throws IOException
	{
		System.out.println("timing1");
		long start = System.nanoTime();
		FileInputStream fis = new FileInputStream(file);
		float[] data = reader.read1D(fis, width * height);
		long readDone = System.nanoTime();
		time1dLoad = readDone - start;
		System.out.println("Timing " + time1dLoad);
	}

	// @Test
	public void timing1D() throws IOException
	{
		System.out.println("timing1d");
		long start = System.nanoTime();
		FileInputStream fis = new FileInputStream(file);
		float[] data = reader.read1D(fis, width * height);
		long readDone = System.nanoTime();
		time1dLoad = readDone - start;
		FloatGrid1D grid = new FloatGrid1D(height, width, data);

		float min = findMin(grid, height, width);
		long minDone = System.nanoTime();
		time1dMin = minDone - readDone;
		long len = data.length;
		float minimum = Float.POSITIVE_INFINITY;
		for (int i = 0; i < len; i++)
		{
			float value = data[i];
			if( !Float.isNaN(value) )
			{
				minimum = minimum > value ? value : minimum;
			}

		}
		long minDirectDone = System.nanoTime();
		time1dMinDirect = minDirectDone - minDone;
		countMissing(grid, height, width);
		long missingDone = System.nanoTime();
		time1dMissing = missingDone - minDirectDone;
		int count = 0;
		for (int i = 0; i < len; i++)
		{
			float value = data[i];
			if( Float.isNaN(value) )
			{
				count++;
			}

		}

	}

	float findMin(FloatGrid grid, int rows, int cols)
	{
		float min = Float.POSITIVE_INFINITY;
		for (int y = 0; y < rows; y++)
		{
			for (int x = 0; x < cols; x++)
			{
				float value = grid.get(x, y);
				if( !Float.isNaN(value) )
				{
					min = min > value ? value : min;
				}
			}
		}
		return min;
	}

	int countMissing(FloatGrid grid, int rows, int cols)
	{
		int count = 0;
		for (int y = 0; y < rows; y++)
		{
			for (int x = 0; x < cols; x++)
			{
				float value = grid.get(x, y);
				if( Float.isNaN(value) )
				{
					count++;
				}
			}
		}
		return count;

	}

	static String toString(long data)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(data / 1000000000L);
		sb.append('.');
		sb.append(data % 1000000000L);
		return sb.toString();
	}

	@AfterClass
	public static void diff()
	{
		System.out.println("1dLoad = " + toString(time1dLoad));
		System.out.println("1dMin  = " + toString(time1dMin));
		System.out.println("1dMinDirect  = " + toString(time1dMinDirect));
		System.out.println("1dMissing  = " + toString(time1dMissing));
		System.out.println("1dMissingDirect  = " + toString(time1dMissingDirect));
		System.out.println("1dVisitorStats  = " + toString(time1dVisitorStats));
		System.out.println("1dMin  = " + toString(time1dMin));
		System.out.println("1dVisitorDir = " + toString(time1dVisitorDir));
		System.out.println();
	}

	// @Test
	public void testLocalReader()
	{
		LocalSRTMhgtSource source = new LocalSRTMhgtSource();
		// boston
		FloatGrid grid = source.getTile(-71, 42);
		assertEquals(0, (int) findMin(grid, height, width));

		// rockies
		grid = source.getTile(-118, 40);
		assertEquals(1052, (int) findMin(grid, height, width));

		// chile
		grid = source.getTile(-70, -30);
		assertEquals(1688, (int) findMin(grid, height, width));

		// croatia
		grid = source.getTile(17, 45);
		assertEquals(67, (int) findMin(grid, height, width));

		// australia
		grid = source.getTile(134, -23);
		assertEquals(446, findMin(grid, height, width));

	}
}
