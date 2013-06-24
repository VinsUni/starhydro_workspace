package app.server.worker;

import java.awt.Frame;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipException;

import javax.swing.ProgressMonitorInputStream;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.data.formats.XYZ;
import star.hydrology.data.interfaces.GridwStat;
import star.hydrology.data.interfaces.Projection;
import star.hydrology.events.UnprojectedMapChangedRaiser;

@SignalComponent()
public class ArcGISWorkerWeb extends ArcGISWorkerWeb_generated implements GISWorker
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

	InputStream getXYZ(String name) throws IOException
	{
		if (frame != null)
		{
			URL url = new URL(URL + workspace + "/" + name);
			return new BufferedInputStream(new ProgressMonitorInputStream(frame, "Reading " + name + " from " + url.getHost(), url.openStream()));
		}
		else
		{
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
		return XYZ.parse(getXYZ(name));
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

	static GISWorker worker;

	public static GISWorker getDefaultWorker()
	{
		if (worker == null)
		{
			worker = new ArcGISWorkerWeb();
		}
		return worker;
	}
}
