package plugin;

/**
 * APIInterface This interface must be implemented by all PLUGINs. This approach allows the default loader to load a PLUGIN and check that it implements the
 * correct interface. By making a PLUGIN serializable remote execution of the PLUGIN is facilitated.
 * 
 * @author cshubert
 */
public interface APIInterface extends java.io.Serializable
{

}
