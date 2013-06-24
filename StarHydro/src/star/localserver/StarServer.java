package star.localserver;

import java.net.BindException;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;

public class StarServer
{

	private Server server;

	private int port ;
	
	public StarServer(Handler handler) throws BindException
	{
		boolean success = false;
		for (int i = 0; i < 4; i++)
		{
			int port = 4997 + 10000 * i;
			success = init(handler, port);			
			if (success)
			{
				this.port = port;
				break;
			}
		}
		if( !success)
		{
			throw new BindException( "Failed to create server.");
		}
	}

	public int getPort()
    {
	    return port;
    }
	
	private boolean init(Handler handler, int port)
	{
		try
		{
			Server server = new Server();
			SocketConnector connector = new SocketConnector();
			connector.setPort(port);
			connector.setHost("127.0.0.1");
			server.addConnector(connector);
			server.setHandler(handler);
			server.setStopAtShutdown(true);
			server.start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
