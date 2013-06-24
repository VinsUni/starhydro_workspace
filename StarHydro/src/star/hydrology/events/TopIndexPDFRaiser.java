package star.hydrology.events;

import star.annotations.Raiser;

@Raiser
public interface TopIndexPDFRaiser extends PDFRaiser
{
	float getMinimumHeight();

	float getMaximumHeight();

	float getPDF(float height);
}
