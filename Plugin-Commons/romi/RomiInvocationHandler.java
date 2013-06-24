package romi;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

import plugin.APIInterface;
import plugin.PluginException;

public class RomiInvocationHandler implements InvocationHandler, RomiWrapperInterface, APIInterface
{
	private static final long serialVersionUID = 1L;

	public final static int DEFAULT_PORT = 7827;
	transient boolean wasConstructorInvoked = false;
	transient String host;
	transient int port;
	transient APIInterface localClass;
	Class myInterface;

	public RomiInvocationHandler(APIInterface local, Class myInterface)
	{
		wasConstructorInvoked = true;
		this.myInterface = myInterface;
		this.localClass = local;
		this.host = null;
		this.port = DEFAULT_PORT;
	}

	public RomiInvocationHandler(String connectionString, Class myInterface)
	{
		wasConstructorInvoked = true;
		this.myInterface = myInterface;
		this.localClass = null;
		if (connectionString.indexOf(':') != -1)
		{
			host = connectionString.substring(0, connectionString.indexOf(':'));
			port = Integer.parseInt(connectionString.substring(connectionString.indexOf(':') + 1));
		}
		else
		{
			host = connectionString;
			port = DEFAULT_PORT;
		}
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws PluginException
	{
		if (wasConstructorInvoked)
		{
			if (localClass != null)
			{
				return invokeLocal(method, localClass, args);
			}
			else
			{
				return invokeRemote(method, args);
			}
		}
		else
		{
			Object localClass = RomiFactory.getSystemRomiWrapper(myInterface);
			return invokeLocal(method, localClass, args);
		}
	}

	Object invokeLocal(Method method, Object localClass, Object[] args) throws PluginException
	{
		try
		{
			// System.out.println( "method " + method.getName() + " local " +
			// localClass + " " + method ) ;
			return method.invoke(localClass, args);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			throw new PluginException(e.getLocalizedMessage());
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
			throw new PluginException(e.getLocalizedMessage());
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
			throw new PluginException(e.getLocalizedMessage());
		}
	}

	Object invokeRemote(Method method, Object[] args) throws PluginException
	{
		Serializable response = null;
		// javax.net.ssl.SSLSocket server = null;
		Socket server = null;
		try
		{
			// server = (javax.net.ssl.SSLSocket)
			// javax.net.ssl.SSLSocketFactory.getDefault().createSocket(host,
			// port);
			// server.setEnabledCipherSuites(server.getSupportedCipherSuites());
			server = new Socket(host, port);
			ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
			out.writeObject(this);
			out.writeObject(method.getName());
			out.writeObject(method.getParameterTypes());
			out.writeObject(args);
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

		if ((response != null) && response instanceof Throwable)
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
		return response;

	}

}
