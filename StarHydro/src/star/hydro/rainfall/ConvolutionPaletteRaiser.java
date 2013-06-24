package star.hydro.rainfall;

import star.annotations.Raiser;
import star.hydrology.ui.palette.Palette;

@Raiser
public interface ConvolutionPaletteRaiser extends star.event.Raiser
{
	Palette getConvolutionPalette();
}
