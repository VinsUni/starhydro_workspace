package utils;

import java.io.File;
import java.lang.reflect.Method;

import utils.jnlp.ServiceManagerWrapper;

public class JNLPHelpers
{

	public static void printArgs(String[] args)
	{
		if (args != null)
		{
			System.out.println("Command line arguments start.");
			for (String s : args)
			{
				System.out.println("Command line arguments: " + s);
			}
			System.out.println("Command line arguments end.");
		}
	}

	public static File parseFileFromArgs(String[] args)
	{
		File ret = null;
		try
		{
			if (args != null && args.length >= 2)
			{
				for (int i = 0; i < args.length; i++)
				{
					if ("-open".equalsIgnoreCase(args[i]))
					{
						ret = new File(args[i + 1]);
						break;
					}
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return ret;
	}

	public static boolean supportIntegrationServices()
	{
		boolean ret = false;
//		if (!OS.isMacOSX())
//		{
//			try
//			{
//				ServiceManagerWrapper w = new ServiceManagerWrapper();
//				ret = w.lookup("javax.jnlp.IntegrationService") != null;
//			}
//			catch (Throwable t)
//			{
//			}
//		}
		return ret;
	}

	public static void requestAssociation(String string, String[] strings)
	{
		System.out.println("todo requestAssociation:" + string + " " + strings);
		try
		{
			ServiceManagerWrapper w = new ServiceManagerWrapper();
			Object is = w.lookup("javax.jnlp.IntegrationService");
			Method m = is.getClass().getMethod("requestAssociation", String.class, String[].class);
			m.invoke(is, string, strings);
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}
}
