package star.hydro.rainfall;

import star.annotations.Raiser;

@Raiser
public interface ConvolutionChartBinsRaiser extends star.event.Raiser
{
	int getNumberOfConvolutionChartBins();
}
