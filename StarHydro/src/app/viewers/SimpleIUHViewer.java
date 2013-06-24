package app.viewers;

import star.annotations.SignalComponent;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.events.interfaces.PaletteRenderableLayer;

@SignalComponent(extend = AbstractMapViewer.class)
public class SimpleIUHViewer extends SimpleIUHViewer_generated
{
	PaletteRenderableLayer colorLayer;
	public PaletteRenderableLayer getLayer()
	{
		return getTerrain();
	}

	public int getKind()
	{
		return LayerConstants.SIMPLEIUHLAYER;
	}

	public String getViewerName()
	{
		return "Simple Instant Unit Hydrograph";
	}
}
