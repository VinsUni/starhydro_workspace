package app.worker.widthfunction;

import app.worker.streamnetwork.Channel;

public class TopographicsWidthFunctionWorker extends AbstractWidthFunctionWorker
{
	float getLength(Channel c)
	{
		return 1;
	}

	public int getType()
	{
		return TOPOLOGIC;
	}

}
