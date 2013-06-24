package star.hydrology.events.regionalization;

import star.annotations.Raiser;
import star.hydrology.events.PDFRaiser;

@Raiser
public interface RegCurvaturePDFRaiser extends PDFRaiser
{
	float getMinimumHeight();

	float getMaximumHeight();

	float getPDF(float height);
}
