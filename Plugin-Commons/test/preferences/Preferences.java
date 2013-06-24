package test.preferences;

import junit.framework.TestCase;
import plugin.Loader;
import plugin.LoaderInterface;
import plugin.PluginException;

public class Preferences extends TestCase
{
	public void test1()
	{
		try
		{
			LoaderInterface loader = Loader.getDefaultLoader();
			String apiName = plugin.preferences.Preferences.class.getName();
			String className = plugin.preferences.PreferencesImplementation.class.getName();
			plugin.preferences.Preferences p = (plugin.preferences.Preferences) loader.getPlugin(apiName, className);
			p.setApplication("StarCommons-test", null);
			java.util.prefs.Preferences pref = p.getPreferences(this.getClass().getName());
			pref.put("test1", "test1");
			assertEquals(pref.get("test1", null), "test1");
			p.setKeyPersistent(this.getClass(), "test2", plugin.preferences.Preferences.PERSIST);
			pref.put("test2", "test2");
			assertEquals(pref.get("test1", null), "test1");
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
		catch( PluginException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
