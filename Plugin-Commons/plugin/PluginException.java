package plugin;

public class PluginException extends Throwable
{

	/**
	 * PluginException All PLUGIN methods must throw a PluginException. All PLUGINs must catch any internally generated exception and rethrow it as a PLUGIN
	 * exception with a standard messages. This form of dealing with exceptions gives the framework using the PLUGIN an opportunity to handle exceptions
	 * generated by the PLUGIN.
	 */
	private static final long serialVersionUID = 9157745801602261980L;
	// Standard messages
	public static final String NULL_APIINTERFACE = "API interface is null.";
	public static final String NULL_PLUGINCLASSNAME = "Plugin classname is null";
	public static final String PLUGIN_DOES_NOT_IMPLEMENT_APIINTERFACE = "Plugin does not implement APIINTERFACE";
	public static final String PLUGIN_LOAD_FAILURE = "Plugin failed to load";

	public PluginException(String message)
	{
		super(message);
	}
}
