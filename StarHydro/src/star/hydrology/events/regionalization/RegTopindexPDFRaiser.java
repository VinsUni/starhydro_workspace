package star.hydrology.events.regionalization;

import star.annotations.Raiser;
import star.hydrology.events.PDFRaiser;

@Raiser
public interface RegTopindexPDFRaiser extends PDFRaiser
{
	float getMinimumHeight();

	float getMaximumHeight();

	float getPDF(float height);
}