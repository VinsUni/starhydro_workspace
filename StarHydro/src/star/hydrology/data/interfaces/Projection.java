package star.hydrology.data.interfaces;

import plugin.APIInterface;

public interface Projection extends APIInterface
{
	String getName();

	String getProjectionParameter(String inputParameters);
}
