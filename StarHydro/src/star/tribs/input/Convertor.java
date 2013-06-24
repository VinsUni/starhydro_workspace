package star.tribs.input;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Convertor
{
	private static class FloatArray
	{
		String time;
		float[] points;

	}

	private static Set<String> getVariables(File tribsVoronoiOutputFolder, String prefix, String[] suffixes) throws IOException
	{
		Set<String> vars = new HashSet<String>();
		for (String suffix : suffixes)
		{
			File sample = new File(tribsVoronoiOutputFolder, MessageFormat.format("{0}0024_00{1}", prefix, suffix));
			BufferedReader reader = new BufferedReader(new InputStreamReader(new java.io.FileInputStream(sample)));
			String header = reader.readLine().trim();
			String[] variables = header.split(",");
			for (String var : variables)
			{
				vars.add(var);
			}
		}
		return vars;
	}

	private static File[] getSortedFileList(File tribsVoronoiOutputFolder)
	{
		File[] files = tribsVoronoiOutputFolder.listFiles();
		Arrays.sort(files, new Comparator<File>()
		{
			public int compare(File o1, File o2)
			{
				return o1.getAbsolutePath().compareToIgnoreCase(o2.getAbsolutePath());
			}
		});
		return files;
	}

	private static Map<String, DataOutputStream> getOutputFiles(File tribsVoronoiOutputFolder, String prefix, String[] suffixes, File cacheFolder) throws IOException
	{
		Map<String, DataOutputStream> outputs = new HashMap<String, DataOutputStream>();
		Set<String> vars = getVariables(tribsVoronoiOutputFolder, prefix, suffixes);
		for (String var : vars)
		{
			outputs.put(var, new DataOutputStream(new FileOutputStream(new File(cacheFolder, var), false)));
		}
		return outputs;
	}

	private static void closeOutputFiles(Map<String, DataOutputStream> outputs) throws IOException
	{
		for (DataOutputStream oos : outputs.values())
		{
			oos.close();
		}
	}

	private static FloatArray[] getFloatArray(int elems, int size)
	{
		FloatArray[] ret = new FloatArray[elems];
		for (int i = 0; i < ret.length; i++)
		{
			ret[i] = new FloatArray();
			ret[i].points = new float[size];
		}
		return ret;
	}

	public static void parse() throws IOException
	{
		File tribsVoronoiOutputFolder = new File("Z:/hydro/R4/Output/voronoi");
		File cacheFolder = new File("C:/tmp/blueriver/");
		String prefix = "blue.";
		String[] suffixes = new String[] { "d", "iv", "dv" };
		int points = 45458;

		Map<String, DataOutputStream> outputs = getOutputFiles(tribsVoronoiOutputFolder, prefix, suffixes, cacheFolder);

		File[] files = getSortedFileList(tribsVoronoiOutputFolder);

		Pattern pattern = Pattern.compile(".*([0-9][0-9][0-9][0-9]_[0-9][0-9])(.*)");
		for (File f : files)
		{
			Matcher m = pattern.matcher(f.getAbsolutePath());
			if (m.matches())
			{
				String time = m.group(1);
				String suffix = m.group(2);
				System.out.println("Reading " + time + " " + suffix);

				BufferedReader reader = new BufferedReader(new InputStreamReader(new java.io.FileInputStream(f)));
				String header = reader.readLine().trim();
				String[] variableNames = header.split(",");
				FloatArray[] data = getFloatArray(variableNames.length, points);

				String line;
				int row = 0;
				while ((line = reader.readLine()) != null)
				{
					line = line.trim();
					String[] v = line.split(",");
					for (int i = 0; i < variableNames.length; i++)
					{
						data[i].points[row] = Float.parseFloat(v[i]);
					}
					row++;
				}
				reader.close();
				reader = null;
				for (int i = 0; i < variableNames.length; i++)
				{
					DataOutputStream oos = outputs.get(variableNames[i]);
					if (oos != null)
					{
						byte[] p = new byte[data[i].points.length*4];
						java.nio.ByteBuffer bb = java.nio.ByteBuffer.wrap(p);
						bb.asFloatBuffer().put(data[i].points);
						System.out.println( time.getBytes().length );
						oos.write( bb.array() ) ;
						oos.flush();
					}
					data[i] = null;
				}
			}
			System.gc();
			System.out.println(Runtime.getRuntime().freeMemory());
		}

		closeOutputFiles(outputs);
	}

	public static void main(String[] args)
	{
		try
		{
			parse();
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}
}
