package star.hydrology.events.regionalization;

import star.annotations.Raiser;
import star.hydrology.events.PDFRaiser;

@Raiser
public interface RegSlopePDFRaiser extends PDFRaiser
{

	float getPDF(float x);
}
