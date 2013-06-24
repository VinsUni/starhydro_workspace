package star.hydrology.events;

import star.annotations.Raiser;

@Raiser
public interface CurvaturePDFRaiser extends PDFRaiser
{
	float getMinimumHeight();

	float getMaximumHeight();

	float getPDF(float height);
}
