package app.worker.widthfunction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.events.StreamRootChangeRaiser;
import star.hydrology.events.WidthFunctionEvent;
import star.hydrology.events.WidthFunctionRaiser;
import app.worker.streamnetwork.Channel;
import app.worker.streamnetwork.Stream;

@SignalComponent()
public abstract class AbstractWidthFunctionWorker extends AbstractWidthFunctionWorker_generated
{
	private Stream root;

	@Handles(raises = { WidthFunctionRaiser.class })
	void setRootStream(StreamRootChangeRaiser r)
	{
		setRootStream(r.getStreamRoot());
	}

	private void setRootStream(Stream root)
	{
		this.root = root;
		updateNumbers();
	}

	private Stream getRootStream()
	{
		return root;
	}

	private TreeSet<ChannelWrapper> set = new TreeSet<ChannelWrapper>();
	private float distance;
	private TreeSet<Datapoint> data = new TreeSet<Datapoint>();

	private void updateNumbers()
	{
		if (getRootStream() != null)
		{
			calculate();
			raise();
		}
		else
		{
			data.clear();
			set.clear();
			raise();
		}
	}

	private void initCalculation()
	{
		data.clear();
		set.clear();
		Channel firstChannel = getRootStream().getParts().get(0);
		set.add(new ChannelWrapper(firstChannel, getLength(firstChannel)));
		distance = 0;
	}

	private void addDatapoint()
	{
		data.add(new Datapoint(set.size(), distance));
	}

	private ArrayList<ChannelWrapper> getSameLengthChannelsAndRemoveFromSet()
	{
		ArrayList<ChannelWrapper> array = new ArrayList<ChannelWrapper>();
		float length = set.first().getLength();
		array.add(set.first());
		set.remove(set.first());
		while (set.size() != 0 && set.first().getLength() == length)
		{
			array.add(set.first());
			set.remove(set.first());
		}
		return array;
	}

	private void addChildren(ArrayList<ChannelWrapper> array)
	{
		Iterator<ChannelWrapper> iter = array.iterator();
		while (iter.hasNext())
		{
			Channel c = iter.next().getChannel();
			Iterator<Channel> childIter = c.getChildren().iterator();
			while (childIter.hasNext())
			{
				Channel child = childIter.next();
				set.add(new ChannelWrapper(child, getLength(child)));
			}
		}

	}

	private void calculate()
	{
		initCalculation();
		while (set.size() != 0)
		{
			addDatapoint();
			float length = set.first().getLength();
			ArrayList<ChannelWrapper> array = getSameLengthChannelsAndRemoveFromSet();
			addChildren(array);
			distance += length;
		}
		addDatapoint();

	}

	abstract float getLength(Channel c);

	public abstract int getType();

	public Set<Datapoint> getData()
	{
		return data;
	}

	private void raise()
	{
		(new WidthFunctionEvent(this)).raise();
	}
}
