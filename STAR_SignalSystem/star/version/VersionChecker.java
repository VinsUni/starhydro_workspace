package star.version;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import utils.FileUtils;

public class VersionChecker
{
	private final static String URL_PATTERN = "http://web.mit.edu/star/swversion/{0}.txt";
	private final static String BUILD_CURRENT = "Build.current";
	private final static String MESSAGE_TITLE = "Message.update.title";
	private final static String MESSAGE_BODY = "Message.update.body";
	private static final String DOWNLOAD_URL = "DownloadURL.current";
	private static final String STARTUP_URL = "Message.startupURL";
	private final static String MESSAGE_TITLE_DEFAULT = "New version of {0} is available";
	private final static String MESSAGE_BODY_DEFAULT = "New version of {0} is available. Would you like to download it from {3}? ";
	public static boolean localOnly = false ;
	
	private static URL getURL(String project) throws MalformedURLException
	{
		return new URL(MessageFormat.format(URL_PATTERN, project));
	}

	private static String getDefaultMessageTitle(String project)
	{
		return MessageFormat.format(MESSAGE_TITLE_DEFAULT, project);
	}

	private static String getDefaultMessageBody(String project, Date currentVersion, Date newVersion, String url)
	{
		return MessageFormat.format(MESSAGE_BODY_DEFAULT, project, currentVersion, newVersion, url);
	}

	public static void check(String projectNameVersion, String project, Date buildDate, JFrame frame)
	{
		try
		{
			if( localOnly )
			{
				if( frame instanceof VersionCheckerDecoration )
				{
					((VersionCheckerDecoration)frame).setVersionCheckerDecoration("NO NETWORK",null);
				}
			}
			URL url = getURL(projectNameVersion);
			Properties properties = new Properties();
			properties.load(url.openStream());
			long currentBuild = Long.parseLong(properties.getProperty(BUILD_CURRENT, "-1"));
			String downloadURL = properties.getProperty(DOWNLOAD_URL, "http://web.mit.edu/star/");
			String startupMessage = "";
			try
			{
				String startupMessageURL = properties.getProperty( STARTUP_URL , "http://starapp.mit.edu/star/swversion/"+projectNameVersion+".message.txt?version=" + buildDate.getTime() + "&build_time=" + URLEncoder.encode( buildDate.toString() , "UTF-8") );
				URL messageURL = new URL( startupMessageURL ) ;
				startupMessage = new String(FileUtils.getStreamToByteArray(messageURL.openStream()));
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
			if (buildDate.getTime() < currentBuild)
			{
				String body = getDefaultMessageBody(project, buildDate, new Date(Long.parseLong(properties.getProperty(BUILD_CURRENT))), downloadURL);
				int ret = JOptionPane.showOptionDialog(frame, properties.getProperty(MESSAGE_BODY, body), properties.getProperty(MESSAGE_TITLE, getDefaultMessageTitle(project)), JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[] { "Open URL in browser", "Cancel" }, "Open URL in browser");
				if (ret == JOptionPane.OK_OPTION)
				{
					utils.UIHelpers.openWebBrowser(downloadURL);
				}
			}
			else if (buildDate.getTime() > currentBuild)
			{
				String preReleaseMessage=  "You are running pre-release version of " + project;
				if( frame instanceof VersionCheckerDecoration )
				{
					String preReleaseMessageShort=  "PRE-RELEASE VERSION";
					((VersionCheckerDecoration)frame).setVersionCheckerDecoration(preReleaseMessageShort,preReleaseMessage);
				}
				else
				{
					JOptionPane.showMessageDialog(frame, preReleaseMessage);					
				}
			}
		}
		catch (RuntimeException ex)
		{
			ex.printStackTrace();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public static void invokeLater(final String projectNameVersion, final String project, final Date buildDate, final JFrame frame)
	{
		Runnable r =new Runnable()
		{
			public void run()
			{
				check(projectNameVersion, project, buildDate, frame);
			}
		};
		new Thread(r).start();
	}
	
	public static boolean processVersionArguments( final String projectNameVersion, final String project, final Date buildDate, String[] args )
	{
		boolean ret = false ;
		if( args != null && args.length == 1 && ("-v".toLowerCase().equals(args[0]) || "--version".toLowerCase().equals(args[0])))
		{
			System.out.println("Product:" + project ) ;
			System.out.println("Version: " + buildDate );
			System.out.println("LongVersion: " + buildDate.getTime() ); 
			ret = true ;
		}
		if(args != null && args.length == 1 && ("-onlylocal").toLowerCase().equals(args[0].toLowerCase()))
		{
			localOnly = true;
			System.setSecurityManager( new SecurityManager() {
				public void checkPermission(java.security.Permission arg0) {
					if( java.net.NetPermission.class == arg0.getClass() )
					{
						java.net.NetPermission p = (java.net.NetPermission)arg0;
						if( p.getName() == "getCookieHandler" )
						{
							throw new SecurityException(arg0.toString());
						}
						System.err.println( p.getClass() + " name:" + p.getName() + " actions:" + p.getActions() );
						return;
//						if( p.getName() == "specifyStreamHandler")
//						{
//							return ;
//						}
//						throw new SecurityException(arg0.toString());
					}
				};
			} );
		}
		return ret; 
	}
}
