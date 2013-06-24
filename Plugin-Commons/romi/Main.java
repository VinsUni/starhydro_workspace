package romi;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Logger;

import plugin.APIInterface;
import plugin.Loader;
import plugin.PluginException;

public class Main extends Thread
{
	public static final String PROPERTIES_FILE = "gateway.properties";
	public static final String PROPERTY_ROMI_HOST = "romi.host";
	public static final String PROPERTY_ROMI_PORT = "romi.port";
	public static final String PROPERTY_LOGFILE = "logfile";
	public static final String PROPERTY_GATEWAY_IMPL = "gateway.impl";
	public static final String PROPERTY_SCHEDULER_IMPL = "scheduler.impl";
	public static final String PROPERTY_SCHEDULER_NODES = "scheduler.nodes";
	public static final String[][] DEFAULT_PROPERTIES = { { PROPERTY_ROMI_HOST, "localhost" }, { PROPERTY_ROMI_PORT, "7905" }, { PROPERTY_LOGFILE, "/dev/null" }, { PROPERTY_GATEWAY_IMPL, "mit.its.hpc.simplegateway" }, { PROPERTY_SCHEDULER_IMPL, "mit.its.hpc.simplescheduler" },
	        { PROPERTY_SCHEDULER_NODES, "192.0.0.2,192.0.0.3" } };

	private Properties mProperties;
	private Logger mLogger;

	public Main()
	{
		mLogger = Logger.getLogger(Main.class.getPackage().toString());
		mProperties = getProperties();
	}

	public void run()
	{
		mLogger.info("Starting Server");

		try
		{
			String gatewayImplPackage = mProperties.getProperty(PROPERTY_GATEWAY_IMPL);
			// String schedulerImplPackage =
			// mProperties.getProperty(PROPERTY_SCHEDULER_IMPL);
			APIInterface gatewayManager = (APIInterface) Loader.getDefaultLoader().getPlugin(gatewayImplPackage, "mit.its.hpc.gateway.GatewayManager");
			RomiWrapper.setLocal(gatewayManager);
		}
		catch (Exception ex)
		{
			mLogger.severe("Error initializing gateway: " + ex.getMessage());
		}
		catch (PluginException osidex)
		{
			mLogger.severe("Error initializing gateway: " + osidex.getMessage());
		}

		InetSocketAddress localAddress = null;
		try
		{
			int port = Integer.parseInt(mProperties.getProperty(PROPERTY_ROMI_PORT));
			String host = mProperties.getProperty(PROPERTY_ROMI_HOST);
			localAddress = new InetSocketAddress(host, port);
		}
		catch (Exception ex)
		{
			mLogger.severe("Could not parse host and/or port: " + ex.getMessage());
			return;
		}

		javax.net.ssl.SSLServerSocket serverSocket = null;
		try
		{
			serverSocket = (javax.net.ssl.SSLServerSocket) javax.net.ssl.SSLServerSocketFactory.getDefault().createServerSocket();
			serverSocket.setEnabledCipherSuites(serverSocket.getSupportedCipherSuites());
			serverSocket.bind(localAddress);
		}
		catch (Exception ex)
		{
			mLogger.severe("Could not start server: " + ex.getMessage());
		}
		mLogger.info("Started: " + serverSocket);

		try
		{
			while (true)
			{
				Socket client = serverSocket.accept();
				ClientHandler handler = new ClientHandler(client);
				handler.start();
			}
		}
		catch (Exception ex)
		{
			mLogger.severe("Error while listening: " + ex.getMessage());
		}
	}

	private Properties getProperties()
	{
		Properties props = new Properties(defaultProperties());
		try
		{
			FileInputStream in = new FileInputStream(PROPERTIES_FILE);
			props.load(in);
		}
		catch (IOException ex)
		{
			mLogger.warning("Could not load properties file: " + ex.getMessage());
			props = defaultProperties();
		}
		return props;
	}

	private Properties defaultProperties()
	{
		Properties props = new Properties();
		for (int i = 0; i < DEFAULT_PROPERTIES.length; i++)
		{
			props.setProperty(DEFAULT_PROPERTIES[i][0], DEFAULT_PROPERTIES[i][1]);
		}
		return props;
	}

	public static void main(String[] qArgs)
	{
		Main main = new Main();
		main.start();
	}
}
