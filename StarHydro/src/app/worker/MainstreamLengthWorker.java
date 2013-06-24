package app.worker;

import java.util.Iterator;

import star.annotations.Handles;
import star.annotations.Properties;
import star.annotations.Property;
import star.annotations.SignalComponent;
import star.hydrology.events.MainStreamLengthEvent;
import star.hydrology.events.MainStreamLengthRaiser;
import star.hydrology.events.StreamRootChangeRaiser;
import app.worker.streamnetwork.Stream;

@SignalComponent()
@Properties( { @Property(name = "rootStream", type = Stream.class), @Property(name = "length", type = float.class, getter = Property.PUBLIC, value = "Float.NaN") })
public class MainstreamLengthWorker extends MainstreamLengthWorker_generated
{
	private void updateNumbers()
	{
		if (getRootStream() != null)
		{
			setLength(0);
			calculate(getRootStream());
			(new MainStreamLengthEvent(this)).raise();
		}
		else
		{
			setLength(Float.NaN);
			(new MainStreamLengthEvent(this, false)).raise();
		}
	}

	private void calculate(Stream stream)
	{
		setLength(getLength() + stream.getLength());
		Iterator<Stream> iter = stream.getChildren().iterator();
		if (iter.hasNext())
		{
			Stream nextChild = iter.next();
			while (iter.hasNext())
			{
				Stream child = iter.next();
				if ((child.getStreamOrder() > nextChild.getStreamOrder()) || (child.getStreamOrder() == nextChild.getStreamOrder() && child.getArea() > nextChild.getArea()))
				{
					nextChild = child;
				}
			}
			calculate(nextChild);
		}
	}

	@Handles(raises = { MainStreamLengthRaiser.class })
	void setStreamRoot(StreamRootChangeRaiser r)
	{
		setRootStream(r.getStreamRoot());
		updateNumbers();
	}
}
