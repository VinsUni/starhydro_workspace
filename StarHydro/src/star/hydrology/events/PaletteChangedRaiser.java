package star.hydrology.events;

import star.annotations.Raiser;
import star.hydrology.events.interfaces.PaletteData;
import star.hydrology.events.interfaces.deep.LayerKind;

@Raiser
public interface PaletteChangedRaiser extends star.event.Raiser, PaletteData, LayerKind
{

}
