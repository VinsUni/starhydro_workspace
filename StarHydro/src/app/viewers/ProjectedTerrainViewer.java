package app.viewers;

import star.hydrology.events.RenderableVisibleRaiser;
import star.hydrology.events.interfaces.LayerConstants;

public class ProjectedTerrainViewer extends AbstractMapViewer implements RenderableVisibleRaiser
{
	public ProjectedTerrainViewer()
	{
		super();
	}

	public int getKind()
	{
		return LayerConstants.TERRAIN;
	}

	public String getViewerName()
	{
		return "Projected Terrain";
	}

}
