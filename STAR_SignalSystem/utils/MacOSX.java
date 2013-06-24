package utils;

import java.awt.Window;
import java.lang.reflect.Method;

import javax.swing.ImageIcon;

public class MacOSX
{

	
	public static void setIcon(ImageIcon icon, Window frame)
	{
		if (OS.isMacOSX())
		{
			try
			{				
				@SuppressWarnings("rawtypes")
                	Class c = Class.forName("com.apple.eawt.Application");
				@SuppressWarnings("unchecked")
                Method factory = c.getMethod("getApplication");
				Object application = factory.invoke(null);
				@SuppressWarnings("unchecked")
                Method setIcon = c.getMethod("setDockIconImage", java.awt.Image.class ) ;
				setIcon.invoke(application, icon.getImage());
			}
			catch (Throwable t)
			{
				t.printStackTrace();
			}
		}
	}

	public static void setTitle(final String title)
    {
		if( OS.isMacOSX() )
		{
			try
			{
				System.setProperty("com.apple.mrj.application.apple.menu.about.name", title );
			}
			catch( Throwable t )
			{
				t.printStackTrace();
			}
		}
    }

}
