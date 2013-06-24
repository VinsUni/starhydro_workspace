package star.hydrology.events;

import star.annotations.Raiser;
import star.hydrology.events.interfaces.LayerConstants;
import star.hydrology.events.interfaces.LayerData;

@Raiser
public interface LayerChangedRaiser extends star.event.Raiser, LayerConstants, LayerData
{
}
