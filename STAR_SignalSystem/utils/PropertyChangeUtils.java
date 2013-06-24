package utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.WeakHashMap;

public class PropertyChangeUtils
{

	private static WeakHashMap<Object, ArrayList<WeakReference<Registration>>> registrations = new WeakHashMap<Object, ArrayList<WeakReference<Registration>>>();

	private static class Registration
	{
		WeakReference<Object> bean;
		WeakReference<PropertyChangeListener> listener;
	}

	private static class PropertyChangeListenerWrapper implements PropertyChangeListener
	{
		private final Runnable runnable;
		private final String property;

		public PropertyChangeListenerWrapper(Runnable r, String property)
		{
			this.runnable = r;
			this.property = property;
		}

		public void propertyChange(PropertyChangeEvent evt)
        {
			if( property == null )
			{
				runnable.run();
			}
			else if( property.equalsIgnoreCase(evt.getPropertyName()))
			{
				runnable.run();
			}
        }
	}

	/**
	 * Registers property change listener with the bean and invokes method upon change
	 * 
	 * @param parent
	 *            object that registers events, used for cleanup purposes upon unregistering
	 * @param bean
	 *            bean with which to register
	 * @param propertyName
	 *            property to register (case insensitive) , null is wildcard
	 * @param method
	 *            method to invoke
	 */
	public static void register(final Object parent, final Object bean, final String propertyName, final Runnable method)
	{
		try
		{
			PropertyChangeListener listener = new PropertyChangeListenerWrapper(method, propertyName);
			bean.getClass().getMethod("addPropertyChangeListener", PropertyChangeListener.class).invoke(bean, listener);
			register(parent, bean, listener);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (SecurityException e)
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
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Unregister all property listeners registered with 'register'
	 * @param parent object used for register
	 */
	public static void unregister(Object parent)
	{
		ArrayList<WeakReference<Registration>> entry = registrations.get(parent);
		if (entry != null)
		{
			for (WeakReference<Registration> regRef : entry)
			{
				Registration reg = regRef.get();
				if (reg != null)
				{
					Object bean = reg.bean.get();
					PropertyChangeListener listener = reg.listener.get();
					if (bean != null && listener != null)
					{
						unregister(bean, listener);
					}
				}
			}
		}
	}

	private static void unregister(Object bean, PropertyChangeListener listener)
	{
		try
		{
			bean.getClass().getMethod("removePropertyChangeListener", PropertyChangeListener.class).invoke(bean, listener);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (SecurityException e)
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
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}
	}

	private static void register(Object parent, Object bean, PropertyChangeListener listener)
	{
		ArrayList<WeakReference<Registration>> entry = registrations.get(parent);
		if (entry == null)
		{
			entry = new ArrayList<WeakReference<Registration>>();
			registrations.put(parent, entry);
		}
		Registration referent = new Registration();
		referent.bean = new WeakReference<Object>(bean);
		referent.listener = new WeakReference<PropertyChangeListener>(listener);
		entry.add(new WeakReference<Registration>(referent));
	}
}
