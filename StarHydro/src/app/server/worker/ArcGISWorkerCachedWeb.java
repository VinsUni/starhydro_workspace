package app.server.worker;

import java.awt.Frame;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

import javax.swing.ProgressMonitorInputStream;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.data.formats.XYZ;
import star.hydrology.data.interfaces.GridwStat;
import star.hydrology.data.interfaces.Projection;
import star.hydrology.events.UnprojectedMapChangedRaiser;

@SignalComponent()
public class ArcGISWorkerCachedWeb extends ArcGISWorkerCachedWeb_generated implements GISWorker
{
	private static final long serialVersionUID = 1L;

	String URL = "http://starapp.mit.edu/star/hydro/datasets/";

	String workspace = "";

	@Handles(raises = {})
	protected void handleEvent(UnprojectedMapChangedRaiser raiser)
	{
		workspace = raiser.getMap().getFilename();
		System.out.println(raiser.getMap().getFilename());
	}

	static GISWorker worker;

	public static GISWorker getDefaultWorker()
	{
		if (worker == null)
		{
			worker = new ArcGISWorkerCachedWeb();
		}
		return worker;
	}

	public boolean fill() throws IOException
	{
		return true;
	}

	public boolean flowAccomulation() throws IOException
	{
		return true;
	}

	public boolean flowDirection() throws IOException
	{
		return true;
	}

	class MyProgressMonitorInputStream extends ProgressMonitorInputStream
	{
		public MyProgressMonitorInputStream(Frame c, String a, InputStream b)
		{
			super(c, a, new BufferedInputStream(b, 16384));
		}

		long last = System.currentTimeMillis();
		long read = 0;

		@Override
		public int read(byte[] b, int off, int len) throws IOException
		{
			long now = System.currentTimeMillis();
			int ret = super.read(b, off, len);
			read += ret;
			float rate = 1.0f * read / (now - last);
			getProgressMonitor().setNote("Downloading at " + rate + "kb/sec");
			return ret;
		}

		public int read(byte[] b) throws IOException
		{
			long now = System.currentTimeMillis();
			int ret = super.read(b);
			read += ret;
			float rate = 1.0f * read / (now - last);
			getProgressMonitor().setNote("Downloading at " + rate + "kb/sec");
			return ret;
		}

		@Override
		public int read() throws IOException
		{
			long now = System.currentTimeMillis();
			read++;
			if (read % 0x3ff == 0)
			{
				float rate = 1.0f * read / (now - last);
				getProgressMonitor().setNote("Downloading at " + rate + "kb/sec");
			}
			return super.read();
		}
	}

	InputStream getXYZ(String name) throws IOException
	{
		if (frame != null)
		{
			System.out.println("getXYZ " + name);
			InputStream receiving = null;
			URL url = new URL(URL + workspace + "/" + name + ".gz");
			URLConnection c = url.openConnection();
			if (c instanceof HttpURLConnection && ((HttpURLConnection) c).getResponseCode() == HttpURLConnection.HTTP_OK)
			{
				receiving = new GZIPInputStream(new BufferedInputStream(c.getInputStream()));
			}
			else
			{
				url = new URL(URL + workspace + "/" + name);
				c = url.openConnection();
				receiving = new BufferedInputStream(c.getInputStream());
			}

			ProgressMonitorInputStream pis = new MyProgressMonitorInputStream(frame, "Reading " + name + " from " + url.getHost(), receiving);
			pis.getProgressMonitor().setMillisToPopup(1);
			pis.getProgressMonitor().setMillisToDecideToPopup(1);
			System.out.println("content length = " + c.getContentLength());
			if (c.getContentLength() != -1)
			{
				pis.getProgressMonitor().setMaximum(c.getContentLength());
			}
			else
			{
				pis.getProgressMonitor().setMaximum(100000000);
			}
			// pis.getProgressMonitor().setNote( "Reading " + url.toExternalForm() ) ;
			return pis;
		}
		else
		{
			System.out.println("getXYZ " + name);
			return (new URL(URL + workspace + "/" + name)).openStream();
		}
	}

	Frame frame = null;

	public void setFrame(Frame f)
	{
		this.frame = f;
	}

	GridwStat getGrid(String name) throws IOException
	{
		GridwStat ret = null;
		System.out.println("Using persistent store...");
		PersistInterface i = null;
		try
		{
			i = (PersistInterface) this.getClass().getClassLoader().loadClass("app.server.worker.JNLPPersist").newInstance();
			if (i != null)
			{
				ret = XYZ.parse(i.openStream(workspace + "/" + name));
				// ret = (GridwStat) i.load(workspace + "/" + name);
			}
		}
		catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		if (ret == null)
		{
			OutputStream os = i.saveStream(workspace + "/" + name);
			ret = XYZ.parse(new CopyInputStream(getXYZ(name), os));
			os.close();
			i.commitStream(workspace + "/" + name);
			// i.save(workspace + "/" + name, ret);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see app.worker.server.GISWorker#getProjected()
	 */
	public GridwStat getProjected() throws IOException
	{
		return getGrid("projected.xyz");
	}

	/*
	 * (non-Javadoc)
	 * @see app.worker.server.GISWorker#getFilled()
	 */
	public GridwStat getFilled() throws IOException
	{
		return getGrid("filled.xyz");
	}

	/*
	 * (non-Javadoc)
	 * @see app.worker.server.GISWorker#getFlowDirection()
	 */
	public GridwStat getFlowDirection() throws IOException
	{
		return getGrid("flowdir.xyz");
	}

	/*
	 * (non-Javadoc)
	 * @see app.worker.server.GISWorker#getFlowAccumulation()
	 */
	public GridwStat getFlowAccumulation() throws IOException
	{
		return getGrid("flowacc.xyz");
	}

	public boolean makeWorkspace() throws IOException
	{
		return true;
	}

	public boolean project(String prefix, Projection outProjection) throws IOException
	{
		return true;
	}

	public boolean removeWorkspace() throws IOException
	{
		return true;
	}

	public boolean unzip(File f) throws IOException
	{
		return true;
	}

	public String prefix(File f) throws ZipException, IOException
	{
		return null;
	}

}
