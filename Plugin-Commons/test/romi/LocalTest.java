package test.romi;

import plugin.PluginException;
import romi.RomiFactory;

public class LocalTest
{
	public static void main(String[] str)
	{
		try
		{
			// test(getLocal());
			test(getRemote());
			test(getRemote());
		}
		catch (PluginException e)
		{
			e.printStackTrace();
		}
	}

	static SimpleInterface getLocal() throws PluginException
	{
		RomiFactory.setSytemLocalObject(SimpleInterface.class, new SimpleClass());
		return (SimpleInterface) RomiFactory.getSystemRomiWrapper(SimpleInterface.class);
	}

	static void test(SimpleInterface myInterface)
	{
		System.out.println(myInterface.concat("Hello ", "world!"));
		System.out.println(myInterface.concat("Hello world! ", 2));
		System.out.println(myInterface.getObject());
		myInterface.setObject("test");
		System.out.println(myInterface.getObject());
	}

	static SimpleInterface getRemote() throws PluginException
	{
		return (SimpleInterface) RomiFactory.getRemoteRomiWrapper(SimpleInterface.class, "localhost");
	}

}
