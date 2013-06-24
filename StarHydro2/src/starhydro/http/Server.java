package starhydro.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Server
{
	final static int PORT = 7367;
	private ServerSocket socket = null;
	private Listener listener = null;
	private boolean running = true;
	private Thread thread = null;

	private double lat;
	private double lng;

	public double lat()
	{
		return lat;
	}

	public double lng()
	{
		return lng;
	}

	class Listener implements Runnable
	{
		public void run()
		{
			running = true;
			Socket s;
			try
			{
				while( running )
				{
					s = socket.accept();
					BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
					String line = null;
					do
					{
						line = r.readLine();
						final String prefix = "/open/";
						if( line.startsWith("GET") && line.indexOf(prefix) != -1 )
						{
							int indexLat = line.indexOf("/open/") + prefix.length();
							int indexLng = line.indexOf("/", indexLat);
							int indexEnd = line.indexOf(" ", indexLng);
							lat = Double.parseDouble(line.substring(indexLat, indexLng));
							lng = Double.parseDouble(line.substring(indexLng + 1, indexEnd));
						}
						System.out.println("Dump line: " + (line != null) + " " + (line != null ? line.length() : 0) + " " + line);
					} while( line != null && line.length() == 0 );
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

					writer.write("<html><b>Thank your, following coordinates are received " + lat + " " + lng + ".</b></html>");

					writer.flush();
					writer.close();
					r.close();
					s.close();
				}
				socket.close();
			}
			catch( SocketException ex )
			{
				running = false;
				listener = null;
				thread = null;
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}

	public Server()
	{
	}

	public boolean init()
	{
		try
		{
			lng = Double.NaN;
			lat = Double.NaN;
			socket = new ServerSocket(PORT, 10, InetAddress.getByAddress(new byte[] {
			        127 , 0 , 0 , 1 }));
			// socket = new ServerSocket(PORT, 10, InetAddress.getLocalHost());
			listener = new Listener();
			thread = new Thread(listener);
			thread.setDaemon(true);
			thread.start();
			return true;
		}
		catch( UnknownHostException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		return false;
	}

	public boolean openBrowser()
	{

		//return org.lwjgl.Sys.openURL("http://web.mit.edu/star/hydro/browsemap.html");
		return false ;
	}

	public boolean close()
	{
		try
		{
			running = false;
			thread.interrupt();
			socket.close();
			return true;
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
		return false;
	}
}
