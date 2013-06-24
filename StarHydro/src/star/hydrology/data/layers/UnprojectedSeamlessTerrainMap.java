package star.hydrology.data.layers;

import star.hydrology.events.interfaces.UnprojectedMap;

public class UnprojectedSeamlessTerrainMap implements UnprojectedMap
{
	private final String filename;

	public UnprojectedSeamlessTerrainMap(String filename)
	{
		super();
		this.filename = filename;
	}

	public String toString()
	{
		// TODO: better
		return filename;
	}

	public String getLayerName()
	{
		// TODO Auto-generated method stub
		return filename;
	}

	public String getFilename()
	{
		// TODO Auto-generated method stub
		return filename;
	}

}
