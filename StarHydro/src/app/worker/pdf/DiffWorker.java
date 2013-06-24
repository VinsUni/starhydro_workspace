package app.worker.pdf;

import star.annotations.SignalComponent;
import star.hydrology.data.interfaces.Grid;
import star.hydrology.data.layers.FloatDataset;
import star.hydrology.data.layers.ProjectedTerrainMap;
import star.hydrology.events.LayerChangedEvent;
import star.hydrology.events.LayerChangedRaiser;
import star.hydrology.events.interfaces.PaletteRenderableLayer;

@SignalComponent(extend = Object.class, raises = { LayerChangedRaiser.class }, handles = { LayerChangedRaiser.class })
public class DiffWorker extends DiffWorker_generated
{
	private PaletteRenderableLayer sourcelayer;
	private PaletteRenderableLayer layer;

	private int sourceLayer = -1;
	private int targetLayer = -1;

	public DiffWorker(int sourceLayer, int targetLayer)
	{
		this.sourceLayer = sourceLayer;
		this.targetLayer = targetLayer;
	}

	public int getSourceLayerKind()
	{
		return sourceLayer;
	}

	public int getLayerKind()
	{
		return targetLayer;
	}

	protected void handleEvent(LayerChangedRaiser raiser)
	{
		if (raiser.getLayerKind() == getSourceLayerKind())
		{
			setSourceLayer(raiser.getLayer());
		}
	}

	public PaletteRenderableLayer getLayer()
	{
		return layer;
	}

	private PaletteRenderableLayer getSourcelayer()
	{
		return sourcelayer;
	}

	private void setSourceLayer(PaletteRenderableLayer sourcelayer)
	{
		this.sourcelayer = sourcelayer;
		compile();
	}

	private void setLayer(PaletteRenderableLayer layer)
	{
		this.layer = layer;
		raise();
	}

	private void compile()
	{
		if (getSourcelayer() != null)
		{
			try
			{
				Grid dataset = getSourcelayer().getDataset();
				FloatDataset target = (FloatDataset) ((FloatDataset) (getSourcelayer().getDataset())).getSameCoverage(FloatDataset.class);
				for (int y = 1; y < target.getRows() - 1; y++)
				{
					for (int x = 1; x < target.getCols() - 1; x++)
					{
						Float p = dataset.getElementAt(x, y);
						Float p0 = dataset.getElementAt(x - 1, y);
						Float p1 = dataset.getElementAt(x + 1, y);
						Float p2 = dataset.getElementAt(x, y + 1);
						Float p3 = dataset.getElementAt(x, y - 1);
						float value = Float.NaN;
						if (!Float.isNaN(p0) && !Float.isNaN(p1) && !Float.isNaN(p2) && !Float.isNaN(p3))
						{
							value = 4 * p - p0 - p1 - p2 - p3;
						}
						target.setElementAt(x, y, value);
					}
				}
				ProjectedTerrainMap layer = new ProjectedTerrainMap();
				layer.setDataset(target);
				layer.setLayerName("PDF Diff ");
				setLayer(layer);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	private void raise()
	{
		(new LayerChangedEvent(this)).raise();
	}

}
