package romi;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import plugin.PluginException;

public class ClientHandler extends Thread
{
	private transient Socket mClient;

	public ClientHandler(Socket qClient)
	{
		if (qClient == null)
		{
			throw new NullPointerException();
		}
		mClient = qClient;
	}

	@SuppressWarnings(value = "unchecked")
	public Object invoke(Object qTarget, String qMethodName, Class[] qParamTypes, Object[] qParams)
	{
		Object result = null;
		try
		{
			Class targetClass = qTarget.getClass();
			Class[] paramClasses = new Class[qParamTypes.length];
			for (int i = 0; i < qParamTypes.length; i++)
			{
				paramClasses[i] = (Class) qParamTypes[i];
			}
			if (qTarget instanceof RomiInvocationHandler)
			{
				RomiInvocationHandler handler = (RomiInvocationHandler) qTarget;
				qTarget = RomiFactory.getSystemRomiWrapper(handler.myInterface);
				java.lang.reflect.Method method = qTarget.getClass().getMethod(qMethodName, paramClasses);
				result = handler.invoke(qTarget, method, qParams);
			}
			else
			{
				java.lang.reflect.Method method = targetClass.getMethod(qMethodName, paramClasses);
				if (method != null)
				{
					result = (Serializable) (method.invoke(qTarget, qParams));
				}
				else
				{
					result = new PluginException("Method " + qMethodName + " not found in " + targetClass);
				}
			}
		}
		catch (Exception ex)
		{
			if (ex instanceof java.lang.reflect.InvocationTargetException)
			{
				result = ex.getCause();
				ex.getCause().printStackTrace();
			}
			else
			{
				result = ex;
				ex.printStackTrace();
			}
		}
		catch (PluginException ex)
		{
			result = ex;
			ex.printStackTrace();
		}
		return result;
	}

	public void run()
	{
		Object result = null;
		try
		{
			ObjectInputStream in = new ObjectInputStream(mClient.getInputStream());
			Serializable obj = (Serializable) in.readObject();
			String methodName = (String) in.readObject();
			// Serializable[] paramTypes = ( Serializable[] )in.readObject();
			Class[] paramTypes = (Class[]) in.readObject();
			Object[] params = (Object[]) in.readObject();
			result = invoke(obj, methodName, paramTypes, params);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			result = ex;
		}
		finally
		{
			try
			{
				ObjectOutputStream out = new ObjectOutputStream(mClient.getOutputStream());
				out.writeObject(result);
				mClient.close();
			}
			catch (Exception ignored)
			{
				ignored.printStackTrace();
			}
		}
	}
}