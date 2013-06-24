package plugin;

/**
 * DefaultLoader Loads a PLUGIN and checks to see that it implements the correct APIInterface.
 * 
 * @author cshubert
 */
public class DefaultLoader implements LoaderInterface
{

	private static final long serialVersionUID = 3935459485487031594L;

	@SuppressWarnings(value = "unchecked")
	public APIInterface getPlugin(String apiInterface, String pluginClassname) throws PluginException
	{
		if (null != apiInterface)
		{
			Class api = null;
			try
			{
				api = Class.forName(apiInterface);
			}
			catch (Throwable ex)
			{
				throw new PluginException(PluginException.PLUGIN_LOAD_FAILURE);
			}
			if (null != pluginClassname)
			{
				Class plugin = null;
				try
				{
					plugin = Class.forName(pluginClassname);
				}
				catch (Throwable ex)
				{
					throw new PluginException(PluginException.PLUGIN_LOAD_FAILURE);
				}
				if (api.isAssignableFrom(plugin))
				{
					try
					{
						return (APIInterface) plugin.newInstance();
					}
					catch (Exception e)
					{
						throw new PluginException(PluginException.PLUGIN_LOAD_FAILURE);
					}
				}
				throw new PluginException(PluginException.PLUGIN_DOES_NOT_IMPLEMENT_APIINTERFACE);
			}
			throw new PluginException(PluginException.NULL_PLUGINCLASSNAME);
		}
		throw new PluginException(PluginException.NULL_APIINTERFACE);
	}

}
