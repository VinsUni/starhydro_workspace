package app.viewers;

import star.hydrology.events.RenderableVisibleRaiser;
import star.hydrology.events.interfaces.LayerConstants;

public class WatershedViewer extends AbstractMapViewer implements RenderableVisibleRaiser
{
	public int getKind()
	{
		return LayerConstants.WATERSHED;
	}

	public String getViewerName()
	{
		return "Watershed";
	}

}