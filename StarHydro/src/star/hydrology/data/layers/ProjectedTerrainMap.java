package star.hydrology.data.layers;

import star.hydrology.data.interfaces.Grid;
import star.hydrology.events.interfaces.PaletteRenderableLayer;

public class ProjectedTerrainMap implements PaletteRenderableLayer
{
	private Grid dataset;
	private String layerName;

	public ProjectedTerrainMap()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public void setXYZDataset(Grid dataset)
	{
		this.dataset = dataset;
	}

	public String getLayerName()
	{
		return layerName;
	}

	public void setLayerName(String layerName)
	{
		this.layerName = layerName;
	}

	/**
	 * @return Returns the dataset.
	 */
	public Grid getDataset()
	{
		return dataset;
	}

	/**
	 * @param dataset
	 *            The dataset to set.
	 */
	public void setDataset(Grid dataset)
	{
		this.dataset = dataset;
	}
}
