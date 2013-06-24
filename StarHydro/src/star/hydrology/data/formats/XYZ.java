package star.hydrology.data.formats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import javax.vecmath.Point3f;

import star.hydrology.data.interfaces.GridRW;
import star.hydrology.data.interfaces.GridwStat;
import star.hydrology.data.layers.FloatDataset;

public class XYZ
{
	public static GridwStat parse(InputStream is) throws IOException
	{
		FloatDataset dataset = new FloatDataset();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line;
		int nCols = 0;
		int nRows = 0;
		double xllcorner = 0;
		double yllcorner = 0;
		double cellSize = 0;
		String nodata = null;
		if (true)
		{
			String prefix = "ncols";
			line = reader.readLine();
			if (line.startsWith(prefix))
			{
				nCols = Integer.parseInt(line.substring(prefix.length()).trim());
			}
		}
		if (true)
		{
			String prefix = "nrows";
			line = reader.readLine();
			if (line.startsWith(prefix))
			{
				nRows = Integer.parseInt(line.substring(prefix.length()).trim());
			}
		}
		if (true)
		{
			String prefix = "xllcorner";
			line = reader.readLine();
			if (line.startsWith(prefix))
			{
				xllcorner = Double.parseDouble(line.substring(prefix.length()).trim());
			}
		}
		if (true)
		{
			String prefix = "yllcorner";
			line = reader.readLine();
			if (line.startsWith(prefix))
			{
				yllcorner = Double.parseDouble(line.substring(prefix.length()).trim());
			}
		}
		if (true)
		{
			String prefix = "cellsize";
			line = reader.readLine();
			if (line.startsWith(prefix))
			{
				cellSize = Double.parseDouble(line.substring(prefix.length()).trim());
			}
		}
		if (true)
		{
			String prefix = "NODATA_value";
			line = reader.readLine();
			if (line.startsWith(prefix))
			{
				nodata = line.substring(prefix.length()).trim();
			}
		}
		dataset.setSize(nCols, nRows);
		dataset.setCellsize((float) cellSize);
		dataset.setCenter(new Point3f((float) (xllcorner + nCols / 2 * cellSize), (float) (yllcorner + nRows / 2 * cellSize), 0f));
		// dataset.setLocation( xllcorner, yllcorner ) ;
		int currentRow = 0;
		while ((line = reader.readLine()) != null)
		{
			addRow(dataset, line, currentRow++, nodata);
		}
		reader.close();
		return dataset;
	}

	static void addRow(GridRW dataset, String line, int row, String nodata)
	{
		StringTokenizer st = new StringTokenizer(line);
		int col = -1;
		while (st.hasMoreElements())
		{

			String token = st.nextToken();
			col++;
			if (nodata != null && nodata.equalsIgnoreCase(token))
			{
				dataset.setElementAt(col, row, Float.NaN);
			}
			else
			{
				try
				{
					float value = Float.parseFloat(token);
					dataset.setElementAt(col, row, value);
				}
				catch (NumberFormatException ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}

}
