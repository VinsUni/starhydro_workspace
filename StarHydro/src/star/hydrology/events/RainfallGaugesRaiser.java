package star.hydrology.events;

import java.awt.Point;
import java.util.Collection;

import star.annotations.Raiser;
import star.hydrology.events.map.MapLayerRaiser;

@Raiser
public interface RainfallGaugesRaiser extends star.event.Raiser, MapLayerRaiser
{
	Collection<Point> getGauges();
}
