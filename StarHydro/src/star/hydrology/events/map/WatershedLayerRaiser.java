package star.hydrology.events.map;

import java.awt.Point;

import star.annotations.Raiser;

@Raiser()
public interface WatershedLayerRaiser extends MapLayerRaiser
{
	Point getWatershedOrigin();
}
