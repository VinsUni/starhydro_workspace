package romi;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import plugin.APIInterface;
import plugin.PluginException;

public class RomiWrapper implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final static boolean DEBUG = false;
	// private final static int SELF = 1 ;
	private final static int PARENT = 2;
	private transient String mRemoteHost;
	private transient int mRemotePort;

	// private static APIInterface mGatewayManager;

	private String getMethodName(int level)
	{
		String methodName = null;
		Throwable t = new Throwable();
		t.fillInStackTrace();
		StackTraceElement[] el = t.getStackTrace();
		if (el != null && el.length > level + 1)
		{
			methodName = el[level].getMethodName();
		}
		else
		{
			throw new RuntimeException("Unable to find method name ");
		}
		if (DEBUG)
		{
			for (int i = 0; i < el.length; i++)
			{
				System.err.println(i + " " + el[i].getClassName() + " " + el[i].getMethodName());
			}
			System.err.println(getClass().getName() + " getMethodName() -> " + methodName);

		}
		return methodName;
	}

	public void setRemote(String qRemoteHost, int qRemotePort)
	{
		if (qRemoteHost == null)
		{
			throw new NullPointerException();
		}
		if (qRemotePort <= 0)
		{
			throw new IllegalArgumentException();
		}
		mRemoteHost = qRemoteHost;
		mRemotePort = qRemotePort;
		// mGatewayManager = null;
	}

	public static void setLocal(APIInterface qGatewayManager)
	{
		if (qGatewayManager == null)
		{
			throw new NullPointerException();
		}
		// mGatewayManager = qGatewayManager;
	}

	public Integer doSomething(Float a, String b) throws PluginException
	{
		if (mRemoteHost == null)
		{
			return doSomethingLocal(a, b);
		}
		else
		{
			return doSomethingRemote(a, b);
		}

	}

	Integer doSomethingLocal(Float a, String b) throws PluginException
	{
		try
		{
			// return mGatewayManager.doSomething( a , b ) ;
			return null;
		}
		catch (Exception ex)
		{
			throw new PluginException("Couldn't open service: " + ex.getMessage());
		}
	}

	Integer doSomethingRemote(Float a, String b) throws PluginException
	{
		Serializable response = null;
		javax.net.ssl.SSLSocket server = null;
		try
		{
			server = (javax.net.ssl.SSLSocket) javax.net.ssl.SSLSocketFactory.getDefault().createSocket(mRemoteHost, mRemotePort);
			server.setEnabledCipherSuites(server.getSupportedCipherSuites());
			ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
			out.writeObject(this);
			out.writeObject(getMethodName(PARENT));
			out.writeObject(new Class[] { Object.class, String.class });
			out.writeObject(new Serializable[] { a, b });
			ObjectInputStream in = new ObjectInputStream(server.getInputStream());
			response = (Serializable) in.readObject();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			response = new PluginException("Invoke failed: " + ex.getMessage());
		}
		finally
		{
			try
			{
				server.close();
			}
			catch (Exception ignorable)
			{
				ignorable.printStackTrace();
			}
		}
		if (!(response instanceof Integer))
		{
			if (response instanceof RuntimeException)
			{
				throw (RuntimeException) response;
			}
			else if (response instanceof PluginException)
			{
				throw (PluginException) response;
			}
			else
			{
				throw new PluginException("Mangled Response: " + response.getClass().toString());
			}
		}
		return (Integer) response;

	}
}
