package app.server.worker;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;

import plugin.APIInterface;
import star.hydrology.data.interfaces.GridwStat;
import star.hydrology.data.interfaces.Projection;

public interface GISWorker extends APIInterface
{

	public abstract boolean removeWorkspace() throws IOException;

	public abstract boolean makeWorkspace() throws IOException;

	public abstract boolean unzip(File f) throws IOException;

	public abstract String prefix(File f) throws ZipException, IOException;

	public abstract boolean project(String prefix, Projection outProjection) throws IOException;

	public abstract boolean fill() throws IOException;

	public abstract boolean flowDirection() throws IOException;

	public abstract boolean flowAccomulation() throws IOException;

	public abstract GridwStat getProjected() throws IOException;

	public abstract GridwStat getFilled() throws IOException;

	public abstract GridwStat getFlowDirection() throws IOException;

	public abstract GridwStat getFlowAccumulation() throws IOException;

}