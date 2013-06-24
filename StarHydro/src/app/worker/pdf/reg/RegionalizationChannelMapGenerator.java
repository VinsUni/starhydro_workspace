package app.worker.pdf.reg;

import java.util.ArrayList;

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
public class RegionalizationChannelMapGenerator extends RegionalizationChannelMapGenerator_generated
{
	private PaletteRenderableLayer layer = null;

	@Handles(raises = { RegionalizeWorkerDiscreteColorMapLayerRaiser.class })
	void discretisizeLayer(RegionalizeWorkerMapLayerRaiser r)
	{
		try
		{
			PaletteRenderableLayer layer = r.getLayer();
			FloatDataset new_layer = (FloatDataset) ((FloatDataset) layer.getDataset()).getSameCoverage(FloatDataset.class);
			Grid set = layer.getDataset();
			ArrayList<Statistics> s = r.getRegionArray();
			int i = 1;
			for (int y = 0; y < set.getRows(); y++)
			{
				for (int x = 0; x < set.getCols(); x++)
				{
					if (!Float.isNaN(set.getElementAt(x, y)))
					{
						Statistics stat = s.get((int) set.getElementAt(x, y));
						if (stat.channel)
						{
							new_layer.setElementAt(x, y, i % 16);
							i++;
						}
						else
						{
							new_layer.setElementAt(x, y, Float.NaN);
						}
					}
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

	private void raise()
	{
		(new RegionalizeWorkerDiscreteColorMapLayerEvent(this)).raise();
	}

	public PaletteRenderableLayer getLayer()
	{
		return layer;
	}

	public int getLayerKind()
	{
		return LayerConstants.REGCHANNELLAYER;
	}
}
