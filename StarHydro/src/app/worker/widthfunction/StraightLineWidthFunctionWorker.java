package app.worker.widthfunction;

import app.worker.streamnetwork.Channel;

public class StraightLineWidthFunctionWorker extends AbstractWidthFunctionWorker
{

	float getLength(Channel c)
	{
		return c.getStraightLineLength();
	}

	public int getType()
	{
		return STRAIGHTLINE;
	}

}
