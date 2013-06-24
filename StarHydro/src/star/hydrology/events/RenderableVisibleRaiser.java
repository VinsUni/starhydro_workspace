package star.hydrology.events;

import javax.media.j3d.BranchGroup;

import star.annotations.Raiser;
import star.hydrology.events.interfaces.RenderableLayer;
import star.hydrology.events.interfaces.Visible;

@Raiser()
public interface RenderableVisibleRaiser extends star.event.Raiser, Visible, RenderableLayer
{
	final int REMOVE = 1;
	final int ADD = 2;
	final int REPLACE = 3;

	public int getAction();

	public int getKind();

	public BranchGroup getOldBranchGroup();
}
