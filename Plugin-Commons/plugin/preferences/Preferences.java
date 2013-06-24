package plugin.preferences;

import java.util.Properties;

import plugin.APIInterface;

public interface Preferences extends APIInterface
{
	final static String PERSIST = "persist";
	final static String TRANSIENT = "transient";

	public void setApplication(String application, Properties defaults);

	public String getApplication();

	public void setKeyPersistent(String absolutePath, String state);

	public void setKeyPersistent(Class clazz, String key, String state);

	public java.util.prefs.Preferences getPreferences(String clazz);

}
