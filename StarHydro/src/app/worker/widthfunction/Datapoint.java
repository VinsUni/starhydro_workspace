package app.worker.widthfunction;

public class Datapoint implements Comparable<Datapoint>
{
	private int width;
	private float distance;

	Datapoint(int w, float d)
	{
		width = w;
		distance = d;
	}

	public String toString()
	{
		return "[W=" + width + " D=" + distance + "]";
	}

	public int compareTo(Datapoint that)
	{
		return Float.compare(this.distance, that.distance);
	}

	public int getWidth()
	{
		return width;
	}

	public float getDistance()
	{
		return distance;
	}
}