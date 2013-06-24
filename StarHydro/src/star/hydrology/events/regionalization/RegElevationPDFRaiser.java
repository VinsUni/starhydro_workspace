package star.hydrology.events.regionalization;

import star.annotations.Raiser;

@Raiser
public interface RegElevationPDFRaiser extends star.hydrology.events.PDFRaiser
{
	float getMinimumHeight();

	float getMaximumHeight();

	float getPDF(float height);
}
