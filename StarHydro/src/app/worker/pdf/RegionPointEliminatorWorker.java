package app.worker.pdf;

import star.annotations.SignalComponent;
import star.hydrology.data.interfaces.Grid;
import star.hydrology.data.layers.FloatDataset;
import star.hydrology.data.layers.ProjectedTerrainMap;
import star.hydrology.events.LayerChangedEvent;
import star.hydrology.events.LayerChangedRaiser;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.events.interfaces.PaletteRenderableLayer;

@SignalComponent(extend = Object.class, raises = { LayerChangedRaiser.class }, handles = { LayerChangedRaiser.class })
public class RegionPointEliminatorWorker extends RegionPointEliminatorWorker_generated
{
	private PaletteRenderableLayer layer = null;

	@Override
	void handleEvent(LayerChangedRaiser raiser)
	{
		if (raiser.getLayerKind() == LayerConstants.REGLAYER)
		{
			eliminate(raiser.getLayer());
		}
	}

	public PaletteRenderableLayer getLayer()
	{
		return layer;
	}

	public int getLayerKind()
	{
		return LayerConstants.VORONOINODES;
	}

	private FloatDataset pointgrid = null;

	private void eliminate(PaletteRenderableLayer data)
	{
		Grid regionsgrid = data.getDataset();
		try
		{
			pointgrid = (FloatDataset) ((FloatDataset) data.getDataset()).getSameCoverage(FloatDataset.class);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		int rows = regionsgrid.getRows();
		int cols = regionsgrid.getCols();
		for (int y = 0; y < rows; y++)
		{
			for (int x = 0; x < cols; x++)
			{
				pointgrid.setElementAt(x, y, regionsgrid.getElementAt(x, y));
			}
		}
		for (int y = 1; y < rows - 1; y++)
		{
			for (int x = 1; x < cols - 1; x++)
			{
				int counter = 0;
				float value = pointgrid.getElementAt(x, y);
				for (int dy = -1; dy <= 1; dy++)
				{
					for (int dx = -1; dx <= 1; dx++)
					{
						counter += value == pointgrid.getElementAt(x + dx, y + dy) ? 1 : 0;
					}
				}
				if (counter == 9)
				{
					pointgrid.setElementAt(x, y, Float.NaN);
				}
			}
		}
		pointgrid.recalculateMinimumAndMaximum();
		ProjectedTerrainMap layer = new ProjectedTerrainMap();
		layer.setLayerName("Regions");
		layer.setDataset(pointgrid);
		this.layer = layer;
		raise();
	}

	private void raise()
	{
		(new LayerChangedEvent(this)).raise();
	}
}
