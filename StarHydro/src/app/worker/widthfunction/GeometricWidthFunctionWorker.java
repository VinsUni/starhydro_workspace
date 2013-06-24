package app.worker.widthfunction;

import app.worker.streamnetwork.Channel;

public class GeometricWidthFunctionWorker extends AbstractWidthFunctionWorker
{

	float getLength(Channel c)
	{
		return c.getLength();
	}

	public int getType()
	{
		return GEOMETRIC;
	}

}
