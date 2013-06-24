package star.hydrology.events;

import star.annotations.Raiser;

@Raiser
public interface ViewerChangeRaiser extends star.event.Raiser
{
	String getViewerName();

	int getKind();
}
