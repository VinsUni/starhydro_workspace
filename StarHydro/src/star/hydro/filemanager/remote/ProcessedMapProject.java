package star.hydro.filemanager.remote;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import app.server.worker.CopyInputStream;
import app.server.worker.JNLPPersist;
import edu.mit.star.plugins.filemanager.interfaces.ProjectException;

public class ProcessedMapProject extends star.hydro.filemanager.common.ProcessedMapProject
{
	String prefix = null;
	String name = null;

	public InputStream getSource() throws ProjectException
	{
		try
		{
			return new CopyInputStream((new URL(prefix + name)).openStream(), (new JNLPPersist()).saveStream(name));
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

	@Override
	protected void postGetParsed(boolean success)
	{
		if (success)
		{
			try
			{
				(new JNLPPersist()).commitStream(name);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

	}

	@Override
	protected void preGetParsed()
	{
		// TODO Auto-generated method stub

	}

}
