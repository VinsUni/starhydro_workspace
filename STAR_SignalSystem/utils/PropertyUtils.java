package utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PropertyUtils
{
	public static Object getValue(Object source, String propertyName)
	{
		Object ret = null;
		for (Method m : source.getClass().getMethods())
		{
			if (m.getName().toLowerCase().equals("get" + propertyName.toLowerCase()) && m.getParameterTypes().length == 0)
			{
				try
                {
	                ret = m.invoke(source, null);
                }
                catch (IllegalArgumentException e)
                {
	                e.printStackTrace();
                }
                catch (IllegalAccessException e)
                {
	                e.printStackTrace();
                }
                catch (InvocationTargetException e)
                {
	                e.printStackTrace();
                }
			}
		}
		return ret;
	}

}
