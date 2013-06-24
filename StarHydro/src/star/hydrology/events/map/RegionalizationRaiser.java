package star.hydrology.events.map;

import star.annotations.Raiser;
import app.viewers.map.RegionalizationModel;

@Raiser
public interface RegionalizationRaiser extends star.event.Raiser
{
	public enum Parameters
	{
		totalRegions, totalPoints,

		networkAccumulationThreshold,

		channelMergeSizeThreshold, hillslopeMergeSizeThreshold,

		channelSplitSizeThreshold, hillslopeSplitSizeThreshold,

		channelElevationMinMax, channelElevationRange, channelCurvatureMinMax, channelCurvatureRange, channelSlopeRange, channelSlopeMinMax, channelTopindexRange, channelTopindexMinMax, channelAspectRange, channelAspectMinMax,

		hillslopeSlopeRange, hillslopeTopindexRange, hillslopeAspectRange, hillslopeElevationRange, hillslopeCurvatureRange, hillslopeSlopeMinMax, hillslopeTopindexMinMax, hillslopeAspectMinMax, hillslopeElevationMinMax, hillslopeCurvatureMinMax,

		mergeRegionsArea, channelCount, hillsideCount, splitRegionsArea, makeConvex, compressRegions, pointsSurviving, regionsMap, hillslopeMap, channelMap
	};

	public RegionalizationModel getParameters();

}
