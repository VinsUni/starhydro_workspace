package app.server.worker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import star.hydrology.data.formats.XYZ;
import star.hydrology.data.interfaces.GridwStat;
import star.hydrology.data.interfaces.Projection;
import star.hydrology.data.projections.UTM;
import utils.Runner;

public class ArcGISWorker implements GISWorker
{

	final int TIMEOUT = Integer.MAX_VALUE;

	private static final long serialVersionUID = 1L;

	String getArcInfoHome()
	{
		return System.getenv("ARCHOME");
	}

	String getArcWorkspaceHome()
	{
		// return System.getenv("TEMP");
		return "\\temp";
	}

	String getArcWorkspaceDirName()
	{
		return "StarHydro";
	}

	boolean rmdir(File directory)
	{
		boolean ret = true;
		File[] list = directory.listFiles();
		for (int i = 0; i < list.length; i++)
		{
			if (list[i].isDirectory())
			{
				ret &= rmdir(list[i]);
				ret &= list[i].delete();
			}
			else
			{
				ret &= list[i].delete();
			}
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see app.worker.server.GISWorker#removeWorkspace()
	 */
	public boolean removeWorkspace() throws IOException
	{
		File tempHome = new File(getArcWorkspaceHome());
		if (tempHome.exists())
		{
			File temp = new File(tempHome, getArcWorkspaceDirName());
			if (temp.exists())
			{
				return rmdir(temp) && temp.delete();
			}
			else
			{
				return true;
			}
		}
		else
		{
			throw new FileNotFoundException("TEMP directory does not exists: TEMP=" + tempHome);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see app.worker.server.GISWorker#makeWorkspace()
	 */
	public boolean makeWorkspace() throws IOException
	{
		File tempHome = new File(getArcWorkspaceHome());
		if (tempHome.exists())
		{
			File temp = new File(tempHome, getArcWorkspaceDirName());
			if (!temp.exists())
			{
				if (temp.exists() || temp.mkdirs())
				{
					String arcCommand = getArcInfoHome() + "/bin/arc";
					Process arc = Runtime.getRuntime().exec(arcCommand);
					Reader is = new InputStreamReader(arc.getInputStream());
					Reader es = new InputStreamReader(arc.getErrorStream());
					Writer os = new OutputStreamWriter(arc.getOutputStream());
					StringBuffer isb = new StringBuffer();
					StringBuffer esb = new StringBuffer();
					read(is, isb, es, esb, "Arc:", 5000, 0);
					write(os, "createworkspace " + temp.toString() + "\n\r" + (char) Character.LINE_SEPARATOR);
					read(is, isb, es, esb, "Arc:", 5000, isb.length());
					write(os, "quit" + nl());
					return read(is, isb, es, esb, "Leaving", 5000, isb.length());
				}
				else
				{
					throw new FileNotFoundException("Can not create temp directory TEMP=" + temp);
				}
			}
			else
			{
				throw new FileNotFoundException("Not cleaned TEMP=" + temp);
			}
		}
		else
		{
			throw new FileNotFoundException("TEMP directory does not exists: TEMP=" + tempHome);
		}
	}

	String nl()
	{
		return "\r\n";
	}

	void write(Writer os, String text) throws IOException
	{
		System.out.println("WRITE: '" + text + "'");
		os.write(text);
		os.flush();
	}

	boolean read(Reader is, StringBuffer isb, Reader es, StringBuffer esb, String trigger, int timeout, int length) throws IOException
	{
		long start = time();
		int DELAY = 100;
		while (isb.indexOf(trigger, length) <= length && (time() - start) < timeout)
		{
			if (is.ready())
			{
				char c = (char) is.read();
				isb.append(c);
				System.out.print(c);
			}
			if (es.ready())
			{
				char c = (char) es.read();
				esb.append(c);
				System.err.print(c);
			}
			if (es.ready() || is.ready())
			{
				Runner.sleep(0);
			}
			else
			{
				Runner.sleep(DELAY);
			}
		}
		return isb.indexOf(trigger) >= length;
	}

	long time()
	{
		return System.currentTimeMillis();
	}

	/*
	 * (non-Javadoc)
	 * @see app.worker.server.GISWorker#unzip(java.io.File)
	 */
	public boolean unzip(File f) throws IOException
	{
		File tempHome = new File(getArcWorkspaceHome());
		if (tempHome.exists())
		{
			File temp = new File(tempHome, getArcWorkspaceDirName());
			if (temp.exists())
			{
				return unzip(f, temp, prefix(f));
			}
			else
			{
				throw new FileNotFoundException("Workspace does not exist TEMP=" + temp);
			}
		}
		else
		{
			throw new FileNotFoundException("TEMP directory does not exists: TEMP=" + tempHome);
		}
	}

	boolean unzip(File f, File outFolder, String prefix) throws IOException
	{

		ZipFile file = new ZipFile(f);
		Enumeration e = file.entries();
		while (e.hasMoreElements())
		{
			ZipEntry entry = (ZipEntry) e.nextElement();
			InputStream is = file.getInputStream(entry);
			String filename = entry.getName();
			write(outFolder, filename.substring(prefix.length()), is);
		}
		file.close();
		return true;
	}

	void write(File outfolder, String name, InputStream is) throws IOException
	{
		int BUFFER = 128 * 1024;
		System.out.println("Writing " + name);
		File f = new File(outfolder, name);
		f.getParentFile().mkdirs();
		FileOutputStream fos = new FileOutputStream(f);
		byte[] array = new byte[BUFFER];
		while (is.available() != 0)
		{
			int size = is.read(array);
			if (size > 0)
			{
				fos.write(array, 0, size);
			}
			if (size == -1)
			{
				break;
			}
		}
		fos.close();
		is.close();
	}

	/*
	 * (non-Javadoc)
	 * @see app.worker.server.GISWorker#prefix(java.io.File)
	 */
	public String prefix(File f) throws ZipException, IOException
	{
		String keyword = "Metadata.xml";
		ZipFile file = new ZipFile(f);
		Enumeration e = file.entries();
		while (e.hasMoreElements())
		{
			String filename = e.nextElement().toString();
			if (filename.endsWith(keyword))
			{
				String prefix = filename.substring(0, filename.length() - keyword.length());
				if (prefix.endsWith("\\") || prefix.endsWith("/"))
				{
					prefix = prefix.substring(0, prefix.length() - 1);
				}
				return prefix;
			}
		}
		file.close();
		return "";
	}

	/*
	 * (non-Javadoc)
	 * @see app.worker.server.GISWorker#project(java.lang.String, star.hydrology.data.interfaces.Projection)
	 */
	public boolean project(String prefix, Projection outProjection) throws IOException
	{
		String projectionFile = "projection.txt";
		File tempHome = new File(getArcWorkspaceHome());
		if (tempHome.exists())
		{
			File temp = new File(tempHome, getArcWorkspaceDirName());
			if (temp.exists())
			{

				String arcCommand = getArcInfoHome() + "/bin/arc";
				Process arc = Runtime.getRuntime().exec(arcCommand);
				Reader is = new InputStreamReader(arc.getInputStream());
				Reader es = new InputStreamReader(arc.getErrorStream());
				Writer os = new OutputStreamWriter(arc.getOutputStream());
				StringBuffer isb = new StringBuffer();
				StringBuffer esb = new StringBuffer();
				read(is, isb, es, esb, "Arc:", TIMEOUT, isb.length());
				write(os, "w " + temp.toString() + nl());
				read(is, isb, es, esb, "Arc:", TIMEOUT, isb.length());
				write(os, "describe " + prefix + nl());
				read(is, isb, es, esb, "Arc:", TIMEOUT, isb.length());
				String inputParameters = projectionParameters(isb.toString());
				String outputParameters = outProjection.getProjectionParameter(inputParameters);
				write(new OutputStreamWriter(new FileOutputStream(new File(temp, projectionFile))), "" + "INPUT" + nl() + inputParameters + nl() + "OUTPUT" + nl() + outputParameters + nl() + "END" + nl());
				write(os, "project grid " + prefix + " projected " + projectionFile + nl());
				read(is, isb, es, esb, "Arc:", TIMEOUT, isb.length());
				write(os, "gridascii projected projected.xyz" + nl());
				read(is, isb, es, esb, "Arc:", TIMEOUT, isb.length());
				write(os, "quit" + nl());
				return read(is, isb, es, esb, "Leaving", TIMEOUT, isb.length());
			}
			else
			{
				throw new FileNotFoundException("Workspace does not exist TEMP=" + temp);
			}
		}
		else
		{
			throw new FileNotFoundException("TEMP directory does not exists: TEMP=" + tempHome);
		}

	}

	/*
	 * (non-Javadoc)
	 * @see app.worker.server.GISWorker#fill()
	 */
	public boolean fill() throws IOException
	{
		File tempHome = new File(getArcWorkspaceHome());
		if (tempHome.exists())
		{
			File temp = new File(tempHome, getArcWorkspaceDirName());
			if (temp.exists())
			{

				String arcCommand = getArcInfoHome() + "/bin/arc";
				Process arc = Runtime.getRuntime().exec(arcCommand);
				Reader is = new InputStreamReader(arc.getInputStream());
				Reader es = new InputStreamReader(arc.getErrorStream());
				Writer os = new OutputStreamWriter(arc.getOutputStream());
				StringBuffer isb = new StringBuffer();
				StringBuffer esb = new StringBuffer();
				read(is, isb, es, esb, "Arc:", TIMEOUT, isb.length());
				write(os, "w " + temp.toString() + nl());
				read(is, isb, es, esb, "Arc:", TIMEOUT, isb.length());
				write(os, "grid" + nl());
				read(is, isb, es, esb, "Grid:", TIMEOUT, isb.length());
				write(os, "fill projected filled sink" + nl());
				read(is, isb, es, esb, "Grid:", TIMEOUT, isb.length());
				write(os, "quit" + nl());
				read(is, isb, es, esb, "Leaving GRID", TIMEOUT, isb.length());
				write(os, "gridascii filled filled.xyz" + nl());
				read(is, isb, es, esb, "Arc:", TIMEOUT, isb.length());
				write(os, "quit" + nl());
				return read(is, isb, es, esb, "Leaving", TIMEOUT, isb.length());
			}
			else
			{
				throw new FileNotFoundException("Workspace does not exist TEMP=" + temp);
			}
		}
		else
		{
			throw new FileNotFoundException("TEMP directory does not exists: TEMP=" + tempHome);
		}

	}

	/*
	 * (non-Javadoc)
	 * @see app.worker.server.GISWorker#flowDirection()
	 */
	public boolean flowDirection() throws IOException
	{
		File tempHome = new File(getArcWorkspaceHome());
		if (tempHome.exists())
		{
			File temp = new File(tempHome, getArcWorkspaceDirName());
			if (temp.exists())
			{

				String arcCommand = getArcInfoHome() + "/bin/arc";
				Process arc = Runtime.getRuntime().exec(arcCommand);
				Reader is = new InputStreamReader(arc.getInputStream());
				Reader es = new InputStreamReader(arc.getErrorStream());
				Writer os = new OutputStreamWriter(arc.getOutputStream());
				StringBuffer isb = new StringBuffer();
				StringBuffer esb = new StringBuffer();
				read(is, isb, es, esb, "Arc:", TIMEOUT, isb.length());
				write(os, "w " + temp.toString() + nl());
				read(is, isb, es, esb, "Arc:", TIMEOUT, isb.length());
				write(os, "grid" + nl());
				read(is, isb, es, esb, "Grid:", TIMEOUT, isb.length());
				write(os, "flowdir = flowdirection( filled )" + nl());
				read(is, isb, es, esb, "Grid:", TIMEOUT, isb.length());
				write(os, "quit" + nl());
				read(is, isb, es, esb, "Leaving GRID", TIMEOUT, isb.length());
				write(os, "gridascii flowdir flowdir.xyz" + nl());
				read(is, isb, es, esb, "Arc:", TIMEOUT, isb.length());
				write(os, "quit" + nl());
				return read(is, isb, es, esb, "Leaving", TIMEOUT, isb.length());
			}
			else
			{
				throw new FileNotFoundException("Workspace does not exist TEMP=" + temp);
			}
		}
		else
		{
			throw new FileNotFoundException("TEMP directory does not exists: TEMP=" + tempHome);
		}

	}

	/*
	 * (non-Javadoc)
	 * @see app.worker.server.GISWorker#flowAccomulation()
	 */
	public boolean flowAccomulation() throws IOException
	{
		File tempHome = new File(getArcWorkspaceHome());
		if (tempHome.exists())
		{
			File temp = new File(tempHome, getArcWorkspaceDirName());
			if (temp.exists())
			{

				String arcCommand = getArcInfoHome() + "/bin/arc";
				Process arc = Runtime.getRuntime().exec(arcCommand);
				Reader is = new InputStreamReader(arc.getInputStream());
				Reader es = new InputStreamReader(arc.getErrorStream());
				Writer os = new OutputStreamWriter(arc.getOutputStream());
				StringBuffer isb = new StringBuffer();
				StringBuffer esb = new StringBuffer();
				read(is, isb, es, esb, "Arc:", TIMEOUT, isb.length());
				write(os, "w " + temp.toString() + nl());
				read(is, isb, es, esb, "Arc:", TIMEOUT, isb.length());
				write(os, "grid" + nl());
				read(is, isb, es, esb, "Grid:", TIMEOUT, isb.length());
				write(os, "flowacc = flowaccumulation( flowdir )" + nl());
				read(is, isb, es, esb, "Grid:", TIMEOUT, isb.length());
				write(os, "quit" + nl());
				read(is, isb, es, esb, "Leaving GRID", TIMEOUT, isb.length());
				write(os, "gridascii flowacc flowacc.xyz" + nl());
				read(is, isb, es, esb, "Arc:", TIMEOUT, isb.length());
				write(os, "quit" + nl());
				return read(is, isb, es, esb, "Leaving", TIMEOUT, isb.length());
			}
			else
			{
				throw new FileNotFoundException("Workspace does not exist TEMP=" + temp);
			}
		}
		else
		{
			throw new FileNotFoundException("TEMP directory does not exists: TEMP=" + tempHome);
		}

	}

	String projectionParameters(String s)
	{
		final String COORD_SYS_DESC = "COORDINATE SYSTEM DESCRIPTION";
		int start = s.indexOf(COORD_SYS_DESC);
		if (start != -1)
		{
			int end = s.indexOf("Arc:", start);
			StringBuffer ret = new StringBuffer(s.substring(start + COORD_SYS_DESC.length(), end));
			String str = "Parameters:";
			if (ret.indexOf(str) != -1)
			{
				ret.setCharAt(ret.indexOf(str) + str.length() - 1, ' ');
			}
			return ret.toString();
		}
		else
		{
			throw new RuntimeException("Can not find " + COORD_SYS_DESC);
		}

	}

	byte[] getXYZ(String name) throws IOException
	{
		File tempHome = new File(getArcWorkspaceHome());
		File temp = new File(tempHome, getArcWorkspaceDirName());
		File file = new File(temp, name);
		byte[] data = new byte[(int) file.length()];
		FileInputStream fis = new FileInputStream(file);
		int current = 0;
		while (current < file.length())
		{
			int read = fis.read(data, current, data.length - current);
			if (read == -1)
			{
				break;
			}
			current += read;
		}
		return data;
	}

	InputStream getXYZStream(String name) throws IOException
	{
		File tempHome = new File(getArcWorkspaceHome());
		File temp = new File(tempHome, getArcWorkspaceDirName());
		File file = new File(temp, name);
		FileInputStream fis = new FileInputStream(file);
		return fis;
	}

	/*
	 * (non-Javadoc)
	 * @see app.worker.server.GISWorker#getProjected()
	 */
	public GridwStat getProjected() throws IOException
	{
		// return XYZ.parse(new ByteArrayInputStream( getXYZ("projected.xyz") )
		// );
		return XYZ.parse(getXYZStream("projected.xyz"));
	}

	/*
	 * (non-Javadoc)
	 * @see app.worker.server.GISWorker#getFilled()
	 */
	public GridwStat getFilled() throws IOException
	{
		return XYZ.parse(getXYZStream("filled.xyz"));
	}

	/*
	 * (non-Javadoc)
	 * @see app.worker.server.GISWorker#getFlowDirection()
	 */
	public GridwStat getFlowDirection() throws IOException
	{
		return XYZ.parse(getXYZStream("flowdir.xyz"));
	}

	/*
	 * (non-Javadoc)
	 * @see app.worker.server.GISWorker#getFlowAccumulation()
	 */
	public GridwStat getFlowAccumulation() throws IOException
	{
		return XYZ.parse(getXYZStream("flowacc.xyz"));
	}

	public static void main(String[] str)
	{
		GISWorker w = new ArcGISWorker();
		try
		{
			File file = new File("C:/workspace/maine/73704518.zip");
			w.removeWorkspace();
			w.makeWorkspace();
			String prefix = w.prefix(file);
			w.unzip(file);
			w.project(prefix, new UTM(18));

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	static GISWorker worker;

	public static GISWorker getDefaultWorker()
	{
		if (worker == null)
		{
			worker = new ArcGISWorker();
		}
		return worker;
	}
}
