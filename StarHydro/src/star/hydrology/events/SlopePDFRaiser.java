package star.hydrology.events;

import star.annotations.Raiser;

@Raiser
public interface SlopePDFRaiser extends PDFRaiser
{
	float getPDF(float height);
}
