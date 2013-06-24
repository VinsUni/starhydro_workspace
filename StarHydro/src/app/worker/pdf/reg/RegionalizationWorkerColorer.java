package app.worker.pdf.reg;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.data.interfaces.Grid;
import star.hydrology.data.layers.FloatDataset;
import star.hydrology.data.layers.ProjectedTerrainMap;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.events.interfaces.PaletteRenderableLayer;
import star.hydrology.events.map.RegionalizeWorkerDiscreteColorMapLayerEvent;
import star.hydrology.events.map.RegionalizeWorkerDiscreteColorMapLayerRaiser;
import star.hydrology.events.map.RegionalizeWorkerMapLayerRaiser;

@SignalComponent()
public class RegionalizationWorkerColorer extends RegionalizationWorkerColorer_generated
{
	PaletteRenderableLayer layer = null;

	@Handles(raises = { RegionalizeWorkerDiscreteColorMapLayerRaiser.class })
	protected void discretisizeLayer(RegionalizeWorkerMapLayerRaiser r)
	{
		try
		{
			PaletteRenderableLayer layer = r.getLayer();
			FloatDataset new_layer = (FloatDataset) ((FloatDataset) layer.getDataset()).getSameCoverage(FloatDataset.class);
			Grid set = layer.getDataset();
			for (int y = 0; y < set.getRows(); y++)
			{
				for (int x = 0; x < set.getCols(); x++)
				{
					new_layer.setElementAt(x, y, set.getElementAt(x, y) % 16);
				}
			}
			ProjectedTerrainMap l = new ProjectedTerrainMap();
			l.setDataset(new_layer);
			l.setLayerName("Reg color layer");
			this.layer = l;
			raise();
		}
		catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void raise()
	{
		(new RegionalizeWorkerDiscreteColorMapLayerEvent(this)).raise();
	}

	public PaletteRenderableLayer getLayer()
	{
		return layer;
	}

	public int getLayerKind()
	{
		return LayerConstants.REGDISCRETELAYER;
	}
}
