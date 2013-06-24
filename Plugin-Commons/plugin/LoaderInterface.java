package plugin;

/**
 * LoaderInterface All PLUGIN loaders must implement this interface. This approach facilitates the writing of special purpose loaders that do more than just
 * load the plugin and check that it implements the appropriate APIInterface
 * 
 * @author cshubert
 */
public interface LoaderInterface extends java.io.Serializable
{

	public APIInterface getPlugin(String apiInterface, String pluginClassname) throws PluginException, PluginException;

}
