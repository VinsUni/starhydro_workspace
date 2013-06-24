package star.hydrology.events;

import star.annotations.Raiser;
import star.hydrology.events.interfaces.ProjectionData;

@Raiser
public interface ProjectionChangedRaiser extends star.event.Raiser, ProjectionData
{
}
