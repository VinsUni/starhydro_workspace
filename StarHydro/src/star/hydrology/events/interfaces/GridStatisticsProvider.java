package star.hydrology.events.interfaces;

import star.hydrology.data.interfaces.GridwStat;

public interface GridStatisticsProvider
{

	GridwStat getGridStatistics();

	void setThreshold(float threshold);

	float getThreshold();

}
