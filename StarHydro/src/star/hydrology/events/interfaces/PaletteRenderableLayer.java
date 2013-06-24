package star.hydrology.events.interfaces;

import star.hydrology.data.interfaces.Grid;

public interface PaletteRenderableLayer
{
	Grid getDataset();

	String getLayerName();
}
