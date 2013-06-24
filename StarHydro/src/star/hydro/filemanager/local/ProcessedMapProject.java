package star.hydro.filemanager.local;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import app.server.worker.JNLPPersist;
import edu.mit.star.plugins.filemanager.interfaces.ProjectException;

public class ProcessedMapProject extends star.hydro.filemanager.common.ProcessedMapProject
{
	String name;

	public InputStream getSource() throws ProjectException
	{
		try
		{
			JNLPPersist persist = new JNLPPersist();
			return persist.openStream(name);
		}
		catch (MalformedURLException e)
		{
			ProjectException ex = new ProjectException();
			ex.initCause(e);
			throw ex;
		}
		catch (IOException e)
		{
			ProjectException ex = new ProjectException();
			ex.initCause(e);
			throw ex;
		}
	}

	protected void postGetParsed(boolean success)
	{
	}

	protected void preGetParsed()
	{
	}

}
