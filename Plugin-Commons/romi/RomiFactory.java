package romi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Hashtable;

import plugin.APIInterface;
import plugin.PluginException;

public class RomiFactory
{
	static Hashtable connectionStrings = new Hashtable();
	static Hashtable localObjects = new Hashtable();

	public static String getSystemConnectionString(Class myInterface)
	{
		return (String) connectionStrings.get(myInterface);
	}

	@SuppressWarnings(value = "unchecked")
	public static void setSystemConnectionString(Class myInterface, String connectionString)
	{
		connectionStrings.put(myInterface, connectionString);
	}

	@SuppressWarnings(value = "unchecked")
	public static void setSytemLocalObject(Class myInterface, APIInterface myLocalObject)
	{
		localObjects.put(myInterface, myLocalObject);
	}

	public static APIInterface getSystemLocalObject(Class myInterface)
	{
		return (APIInterface) localObjects.get(myInterface);
	}

	public static Object getSystemRomiWrapper(Class myInterface) throws PluginException
	{
		if (null != getSystemLocalObject(myInterface))
		{
			return getLocalRomiWrapper(myInterface, getSystemLocalObject(myInterface));
		}
		else if (null != getSystemConnectionString(myInterface))
		{
			return getRemoteRomiWrapper(myInterface, getSystemConnectionString(myInterface));
		}
		else
		{
			throw new PluginException("Can not find matching proxy for " + myInterface);
		}
	}

	@SuppressWarnings(value = "unchecked")
	public static Object getLocalRomiWrapper(Class myInterface, APIInterface myLocalObject) throws PluginException
	{
		InvocationHandler h = new RomiInvocationHandler(myLocalObject, myInterface);
		Class proxyClass = Proxy.getProxyClass(myInterface.getClassLoader(), new Class[] { myInterface });
		try
		{
			return proxyClass.getConstructor(new Class[] { InvocationHandler.class }).newInstance(new Object[] { h });
		}
		catch (IllegalArgumentException e)
		{
			throw new PluginException(e.getLocalizedMessage());
		}
		catch (SecurityException e)
		{
			throw new PluginException(e.getLocalizedMessage());
		}
		catch (InstantiationException e)
		{
			throw new PluginException(e.getLocalizedMessage());
		}
		catch (IllegalAccessException e)
		{
			throw new PluginException(e.getLocalizedMessage());
		}
		catch (InvocationTargetException e)
		{
			throw new PluginException(e.getLocalizedMessage());
		}
		catch (NoSuchMethodException e)
		{
			throw new PluginException(e.getLocalizedMessage());
		}

	}

	@SuppressWarnings(value = "unchecked")
	public static Object getRemoteRomiWrapper(Class myInterface, String connectionString) throws PluginException
	{
		InvocationHandler h = new RomiInvocationHandler(connectionString, myInterface);
		Class proxyClass = Proxy.getProxyClass(myInterface.getClassLoader(), new Class[] { myInterface });
		try
		{
			return proxyClass.getConstructor(new Class[] { InvocationHandler.class }).newInstance(new Object[] { h });
		}
		catch (IllegalArgumentException e)
		{
			throw new PluginException(e.getLocalizedMessage());
		}
		catch (SecurityException e)
		{
			throw new PluginException(e.getLocalizedMessage());
		}
		catch (InstantiationException e)
		{
			throw new PluginException(e.getLocalizedMessage());
		}
		catch (IllegalAccessException e)
		{
			throw new PluginException(e.getLocalizedMessage());
		}
		catch (InvocationTargetException e)
		{
			throw new PluginException(e.getLocalizedMessage());
		}
		catch (NoSuchMethodException e)
		{
			throw new PluginException(e.getLocalizedMessage());
		}
	}

}
