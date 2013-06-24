package app.worker;

import java.util.Iterator;

import star.annotations.Handles;
import star.annotations.Properties;
import star.annotations.Property;
import star.annotations.SignalComponent;
import star.hydrology.events.StreamOrderStatisticsEvent;
import star.hydrology.events.StreamOrderStatisticsRaiser;
import star.hydrology.events.StreamRootChangeRaiser;
import app.worker.streamnetwork.Stream;

@SignalComponent()
@Properties( { @Property(name = "rootStream", type = Stream.class, getter = Property.PUBLIC), @Property(name = "orderCount", type = int[].class, getter = Property.PUBLIC), @Property(name = "orderLength", type = float[].class, getter = Property.PUBLIC),
        @Property(name = "orderAreas", type = float[].class, getter = Property.PUBLIC), @Property(name = "orderSlopes", type = float[].class, getter = Property.PUBLIC) })
public class StreamOrderStatisticsWorker extends StreamOrderStatisticsWorker_generated
{
	@Handles(raises = { StreamOrderStatisticsRaiser.class })
	void setStreamRoot(StreamRootChangeRaiser r)
	{
		setRootStream(r.getStreamRoot());
		updateNumbers();
	}

	private void updateNumbers()
	{
		initialize();
		if (getRootStream() != null)
		{
			calculate(getRootStream());
			normalize();
		}
		raise();
	}

	private void normalize()
	{
		for (int i = 0; i < getOrderCount().length; i++)
		{
			if (getOrderCount()[i] != 0)
			{
				getOrderLength()[i] /= getOrderCount()[i];
				getOrderAreas()[i] /= getOrderCount()[i];
				getOrderSlopes()[i] /= getOrderCount()[i];
			}
		}
	}

	private void initialize()
	{
		int maxOrder = getRootStream() != null ? getRootStream().getStreamOrder() : 0;
		setOrderCount(new int[maxOrder + 1]);
		setOrderLength(new float[maxOrder + 1]);
		setOrderAreas(new float[maxOrder + 1]);
		setOrderSlopes(new float[maxOrder + 1]);
	}

	private void calculate(Stream stream)
	{
		int order = stream.getStreamOrder();
		getOrderCount()[order]++;
		getOrderLength()[order] += stream.getLength();
		getOrderAreas()[order] += stream.getArea();
		float[] outlet = stream.getOutletHeight();
		float[] peak = stream.getPeakPoint();
		float height = peak[2] - outlet[2];
		float distance = (float) Math.sqrt((peak[1] - outlet[1]) * (peak[1] - outlet[1]) + (peak[0] - outlet[0]) * (peak[0] - outlet[0]));
		if (distance != 0)
		{
			getOrderSlopes()[order] += height / distance;
		}
		else
		{
			// (new RuntimeException("Order slope drop for " + stream)).printStackTrace();
		}
		Iterator<Stream> iter = stream.getChildren().iterator();
		while (iter.hasNext())
		{
			Stream child = iter.next();
			calculate(child);
		}
	}

	private void raise()
	{
		(new StreamOrderStatisticsEvent(this)).raise();
	}

}
