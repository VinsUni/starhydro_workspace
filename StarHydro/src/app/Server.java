package app;

import java.net.ServerSocket;
import java.net.Socket;

import romi.ClientHandler;
import romi.RomiFactory;
import romi.RomiInvocationHandler;
import app.server.worker.ArcGISWorker;
import app.server.worker.GISWorker;

public class Server
{
	public static void main(String str[])
	{
		RomiFactory.setSytemLocalObject(GISWorker.class, ArcGISWorker.getDefaultWorker());
		(new Server()).run();
	}

	public void run()
	{
		try
		{
			System.out.println("Hydro server running");
			int port = RomiInvocationHandler.DEFAULT_PORT;
			ServerSocket socket = new ServerSocket(port);
			while (true)
			{
				Socket client = socket.accept();
				ClientHandler handler = new ClientHandler(client);
				System.out.println("Handle event from " + client);
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
