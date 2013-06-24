package app.worker.widthfunction;

import app.worker.streamnetwork.Channel;

class ChannelWrapper implements Comparable<ChannelWrapper>
{
	private static int counter;
	private Channel channel;
	private float length;
	private int sequence;

	ChannelWrapper(Channel c, float l)
	{
		channel = c;
		length = l;
		sequence = counter++;
	}

	public int compareTo(ChannelWrapper that)
	{
		int ret = Float.compare(this.length, that.length);
		return ret != 0 ? ret : Float.compare(this.sequence, that.sequence);
	}

	float getLength()
	{
		return length;
	}

	Channel getChannel()
	{
		return channel;
	}

	public boolean equals(Object o)
	{
		if (o instanceof ChannelWrapper)
		{
			ChannelWrapper that = (ChannelWrapper) o;
			return (that.channel == this.channel && that.length == this.length);
		}
		return false;
	}
}
