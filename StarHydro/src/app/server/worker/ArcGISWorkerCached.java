package app.server.worker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.data.formats.XYZ;
import star.hydrology.data.interfaces.GridwStat;
import star.hydrology.data.interfaces.Projection;
import star.hydrology.data.projections.UTM;
import star.hydrology.events.UnprojectedMapChangedRaiser;
import utils.Runner;

@SignalComponent()
public class ArcGISWorkerCached extends ArcGISWorkerCached_generated implements GISWorker
{

	private static final long serialVersionUID = 1L;
	String workspace = "StarHydro";

	String getArcInfoHome()
	{
		return System.getenv("ARCHOME");
	}

	String getArcWorkspaceHome()
	{
		String ret = "";
		if ((new File("\\temp")).exists() && (new File("\\temp")).canWrite())
		{
			ret = "\\temp";
		}
		else
		{
			if (null == System.getenv("HOME"))
			{
				ret = System.getenv("USERPROFILE");
			}
			else
			{
				ret = System.getenv("HOME");
			}
		}
		return ret != null ? ret : "";
	}

	String getArcWorkspaceDirName()
	{
		return workspace;
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
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see app.worker.server.GISWorker#makeWorkspace()
	 */
	public boolean makeWorkspace() throws IOException
	{
		return true;
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
		boolean active;
		while (isb.indexOf(trigger, length) <= length && (time() - start) < timeout)
		{
			active = false;
			if (is.ready())
			{
				char c = (char) is.read();
				isb.append(c);
				System.out.print(c);
				active = true;
			}
			if (es.ready())
			{
				char c = (char) es.read();
				esb.append(c);
				System.err.print(c);
				active = true;
			}
			if (!active)
			{
				Runner.sleep(1);
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
		return true;
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
		/*
		 * String keyword = "Metadata.xml"; ZipFile file = new ZipFile(f); Enumeration e = file.entries(); while (e.hasMoreElements()) { String filename =
		 * e.nextElement().toString(); if (filename.endsWith(keyword)) { String prefix = filename.substring(0, filename.length() - keyword.length()); if
		 * (prefix.endsWith("\\") || prefix.endsWith("/")) { prefix = prefix.substring(0, prefix.length() - 1); } return prefix; } } file.close();
		 */
		return "";
	}

	/*
	 * (non-Javadoc)
	 * @see app.worker.server.GISWorker#project(java.lang.String, star.hydrology.data.interfaces.Projection)
	 */
	public boolean project(String prefix, Projection outProjection) throws IOException
	{
		File tempHome = new File(getArcWorkspaceHome());
		if (tempHome.exists())
		{
			File temp = new File(tempHome, getArcWorkspaceDirName());
			if (temp.exists())
			{
				return true;
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
				return true;
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
				return true;
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
				return true;
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

	InputStream getXYZ(String name) throws IOException
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
		return XYZ.parse(getXYZ("projected.xyz"));
	}

	/*
	 * (non-Javadoc)
	 * @see app.worker.server.GISWorker#getFilled()
	 */
	public GridwStat getFilled() throws IOException
	{
		return XYZ.parse(getXYZ("filled.xyz"));
	}

	/*
	 * (non-Javadoc)
	 * @see app.worker.server.GISWorker#getFlowDirection()
	 */
	public GridwStat getFlowDirection() throws IOException
	{
		return XYZ.parse(getXYZ("flowdir.xyz"));
	}

	/*
	 * (non-Javadoc)
	 * @see app.worker.server.GISWorker#getFlowAccumulation()
	 */
	public GridwStat getFlowAccumulation() throws IOException
	{
		return XYZ.parse(getXYZ("flowacc.xyz"));
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
			worker = new ArcGISWorkerCached();
		}
		return worker;
	}

	@Handles(raises = {})
	protected void handleEvent(UnprojectedMapChangedRaiser raiser)
	{
		workspace = raiser.getMap().getFilename();
		System.out.println(raiser.getMap().getFilename());
	}
}
