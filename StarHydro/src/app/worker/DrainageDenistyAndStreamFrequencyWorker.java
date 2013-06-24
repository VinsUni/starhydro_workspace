package app.worker;

import star.annotations.Handles;
import star.annotations.Properties;
import star.annotations.Property;
import star.annotations.SignalComponent;
import star.hydrology.events.DrainageDensityAndStreamFrequencyEvent;
import star.hydrology.events.DrainageDensityAndStreamFrequencyRaiser;
import star.hydrology.events.StreamOrderStatisticsRaiser;

@SignalComponent()
@Properties( { @Property(name = "orderArea", type = float[].class), @Property(name = "orderLength", type = float[].class), @Property(name = "orderCount", type = int[].class), @Property(name = "drainageDensity", type = float.class, getter = Property.PUBLIC),
        @Property(name = "streamFrequency", type = float.class, getter = Property.PUBLIC)

})
public class DrainageDenistyAndStreamFrequencyWorker extends DrainageDenistyAndStreamFrequencyWorker_generated
{
	private void updateNumbers()
	{
		float drainageDensity = 0;
		float streamFrequency = 0;
		for (int i = 1; i < getNumberOfOrders(); i++)
		{
			drainageDensity += getOrderLength(i) / getOrderArea(1);
			streamFrequency += getOrderCount(i) / getOrderArea(1);
		}
		if (utils.Runner.isInterrupted())
		{
			return;
		}
		setStreamFrequency(streamFrequency);
		setDrainageDensity(drainageDensity);
		raise();
	}

	private int getNumberOfOrders()
	{
		return getOrderCount() != null ? getOrderCount().length : 0;
	}

	private void raise()
	{
		(new DrainageDensityAndStreamFrequencyEvent(this)).raise();
	}

	@Handles(raises = { DrainageDensityAndStreamFrequencyRaiser.class })
	void setStats(StreamOrderStatisticsRaiser r)
	{
		setOrderArea(r.getOrderAreas());
		setOrderCount(r.getOrderCount());
		setOrderLength(r.getOrderLength());
		updateNumbers();
	}
}
