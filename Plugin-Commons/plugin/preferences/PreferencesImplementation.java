package plugin.preferences;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

public class PreferencesImplementation implements Preferences
{
	private static final long serialVersionUID = 1L;
	static Preferences myImpl = null;
	String application = null;
	Properties defaults = null;

	public PreferencesImplementation()
	{
		if (myImpl == null)
		{
			myImpl = this;
		}
	}

	public String getApplication()
	{
		if (this != myImpl)
		{
			return myImpl.getApplication();
		}
		else
		{
			return application;
		}
	}

	public java.util.prefs.Preferences getPreferences(String clazz)
	{
		if (this != myImpl)
		{
			return myImpl.getPreferences(clazz);
		}
		else
		{

			return (new JavaPreferencesImpl(null, "", this)).node((getApplication() + "/" + clazz).toLowerCase());
		}
	}

	public void setApplication(String application, Properties defaults)
	{
		if (this != myImpl)
		{
			myImpl.setApplication(application, defaults);
		}
		else
		{
			this.application = application;
			this.defaults = defaults;
		}
	}

	protected boolean isKeyPersistent(String key)
	{
		String node = ("/" + getApplication() + "/" + this.getClass().getName()).toLowerCase();
		String state = java.util.prefs.Preferences.userRoot().node(node).get(key, null);
		if (state == null)
		{
			state = getPreferences(this.getClass().getName()).get(key, TRANSIENT);
		}
		if (TRANSIENT.equals(state) && key.indexOf(JavaPreferencesImpl.SEPARATOR) != -1 )
		{
			state = getPreferences(this.getClass().getName()).get(key.substring(0, key.lastIndexOf(JavaPreferencesImpl.SEPARATOR)), TRANSIENT);
		}
		return (PERSIST.equalsIgnoreCase(state));
	}

	public void setKeyPersistent(String key, String state)
	{
		java.util.prefs.Preferences.userRoot().node(this.getClass().getName().toLowerCase()).put(key.toLowerCase(), state);
	}

	public void setKeyPersistent(Class clazz, String key, String state)
	{
		java.util.prefs.Preferences.userRoot().node(("/" + getApplication() + "/" + this.getClass().getName()).toLowerCase()).put(("/" + getApplication() + "/" + clazz.getName() + ":" + key).toLowerCase(), state);
	}

	protected String get(String key)
	{
		return defaults != null ? defaults.getProperty(key.toLowerCase(), null) : null;
	}

	protected ArrayList<String> keys(String key)
	{
		if (defaults != null)
		{
			ArrayList<String> ret = new ArrayList<String>();
			Iterator<Object> s = defaults.keySet().iterator();
			String keyPrefix = key.toLowerCase();
			while (s.hasNext())
			{
				String tkey = s.next().toString();
				if (tkey.startsWith(keyPrefix))
				{
					ret.add(tkey.substring(keyPrefix.length()));
				}
			}
			return ret;
		}
		else
		{
			return new ArrayList<String>();
		}
	}

}
