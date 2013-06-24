package plugin.preferences;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class JavaPreferencesImpl extends AbstractPreferences
{
	static char SEPARATOR = '*';
	private transient Preferences persistentPreferences = null;
	HashMap<String, String> transientPreferences = null;
	HashMap<String, JavaPreferencesImpl> children = new HashMap<String, JavaPreferencesImpl>();
	PreferencesImplementation pluginImpl = null;

	public JavaPreferencesImpl(JavaPreferencesImpl parent, String name, PreferencesImplementation pluginImpl)
	{
		super(parent, name);
		this.pluginImpl = pluginImpl;
	}

	Preferences getPersistentPreferences()
	{
		if (persistentPreferences == null)
		{
			persistentPreferences = Preferences.userRoot().node(absolutePath());
		}
		return persistentPreferences;
	}

	Map<String, String> getTransientPreferences()
	{
		if (transientPreferences == null)
		{
			transientPreferences = new HashMap<String, String>();
		}
		return transientPreferences;
	}

	@Override
	protected AbstractPreferences childSpi(String name)
	{
		JavaPreferencesImpl pref = new JavaPreferencesImpl(this, name, pluginImpl);
		children.put(name, pref);
		return pref;
	}

	@Override
	protected String[] childrenNamesSpi() throws BackingStoreException
	{
		TreeSet<String> ret = new TreeSet<String>();
		ret.addAll(children.keySet());
		ret.addAll(Arrays.asList(getPersistentPreferences().childrenNames()));
		ret.addAll(pluginImpl.keys(absolutePath() + SEPARATOR));
		return ret.toArray(new String[0]);
	}

	@Override
	protected void flushSpi() throws BackingStoreException
	{
		getPersistentPreferences().flush();
	}

	@Override
	protected String getSpi(String key)
	{
		String value = getTransientPreferences().get(key);
		if (value == null)
		{
			value = getPersistentPreferences().get(key, null);
		}
		if (value == null)
		{
			value = pluginImpl.get(absolutePath() + SEPARATOR + key);
		}
		//System.out.println( value + ")") ;
		return value;
	}

	@Override
	protected String[] keysSpi() throws BackingStoreException
	{
		TreeSet<String> ret = new TreeSet<String>();
		ret.addAll(getTransientPreferences().keySet());
		ret.addAll(Arrays.asList(getPersistentPreferences().keys()));
		ret.addAll(pluginImpl.keys(absolutePath() + SEPARATOR));
		return ret.toArray(new String[0]);
	}

	@Override
	protected void putSpi(String key, String value)
	{
		if (pluginImpl.isKeyPersistent(absolutePath() + SEPARATOR + key))
		{
			System.out.println( "put as persistent " + absolutePath() + SEPARATOR + key + " -- " + key + " " + value );
			getPersistentPreferences().put(key, value);
		}
		else
		{
			System.out.println( "put as transient " + absolutePath() + SEPARATOR + key + " -- " + key + " " + value );
			getTransientPreferences().put(key, value);
		}
	}

	@Override
	protected void removeNodeSpi() throws BackingStoreException
	{
		getPersistentPreferences().removeNode();
		getTransientPreferences().clear();
	}

	@Override
	protected void removeSpi(String key)
	{
		getPersistentPreferences().remove(key);
		getTransientPreferences().remove(key);
	}

	@Override
	protected void syncSpi() throws BackingStoreException
	{
		getPersistentPreferences().sync();
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("application: " + pluginImpl.application.toString());
		sb.append("\n");
		sb.append("defaults: " + pluginImpl.defaults.toString());
		sb.append("\n");
		SW sw = new SW();
		try
		{
			exportSubtree(sw);
			sb.append(sw);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (BackingStoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sb.toString();
	}

	class SW extends java.io.OutputStream
	{
		StringWriter sw = new StringWriter();

		@Override
		public void write(int b) throws IOException
		{
			sw.write(b);
		}

		@Override
		public String toString()
		{
			return sw.toString();
		}

	}

}
