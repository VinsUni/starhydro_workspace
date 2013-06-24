package test.romi;

import java.net.ServerSocket;
import java.net.Socket;

import plugin.PluginException;
import romi.ClientHandler;
import romi.RomiFactory;
import romi.RomiInvocationHandler;

public class Server
{
	public static void main(String str[])
	{
		try
		{
			getLocal();
			(new Server()).run();
		}
		catch (PluginException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static SimpleInterface getLocal() throws PluginException
	{
		RomiFactory.setSytemLocalObject(SimpleInterface.class, new SimpleClass());
		return (SimpleInterface) RomiFactory.getSystemRomiWrapper(SimpleInterface.class);
	}

	public void run()
	{
		try
		{
			int port = RomiInvocationHandler.DEFAULT_PORT;
			ServerSocket socket = new ServerSocket(port);
			while (true)
			{
				Socket client = socket.accept();
				ClientHandler handler = new ClientHandler(client);
				handler.start();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return;
		}

	}
}
