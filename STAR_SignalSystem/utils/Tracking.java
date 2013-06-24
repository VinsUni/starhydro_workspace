package utils;

import java.awt.Dimension;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Random;

import javax.swing.ImageIcon;

public class Tracking
{
	static Random random = new Random();
	static int utmvid = random.nextInt(999999);
	static int utms = 0;

	static String screenResolution = null;

	public static String getScreenResolution()
	{
		if (screenResolution == null)
		{
			try
			{
				Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
				screenResolution = MessageFormat.format("{0}x{1}", d.width, d.height);
			}
			catch (Exception ex)
			{

			}
			catch (Error er)
			{

			}
		}
		return screenResolution == null ? "-" : screenResolution;
	}

	public static ImageIcon track(String application, String event)
	{
		String GA_ACCOUNT = "UA-1048253-18";
		try
		{
			final String path = application + "/" + event;
			long rnd1 = random.nextInt(2147483645 - 1147483647) + 1147483647;
			long time1 = System.currentTimeMillis() / 1000;
			String utmcc = MessageFormat.format("__utma%3D{0}.{1,number,############}.{2,number,############}.{3,number,############}.{4,number,############}.1%3B", "262859854", rnd1, time1, time1, time1);
			final String url = MessageFormat.format("http://www.google-analytics.com/__utm.gif?utmwv={0}&utmn={1,number,############}&utmhn=starapp.mit.edu&utmr={2}&utmp={3}&utmac={4}&utmcc={5}&utmvid={6,number,############}&utms={7,number,#######}&utmsr={8}&guid=ON", //
			        "4.4sh", random.nextInt() & 0x7fffffff, URLEncoder.encode("-", "UTF-8"), URLEncoder.encode(path, "UTF-8"), GA_ACCOUNT, utmcc, utmvid, utms, getScreenResolution());
			utms++;
			new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						java.net.URL url2 = new java.net.URL(url);
						// url2.openConnection();
						InputStream is = url2.openStream();
						byte[] data = FileUtils.getStreamToByteArray(is);
						System.out.println("tracking " + path + " " + data.length);
					}
					catch (Throwable t)
					{
						t.printStackTrace();
					}
				};
			}).start();

			return null;
		}
		catch (Throwable ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
}
