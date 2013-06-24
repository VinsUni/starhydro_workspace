package star.event;

import java.lang.Thread.UncaughtExceptionHandler;

import star.events.common.DistributionExceptionEvent;
import star.events.common.DistributionExceptionRaiser;

public class UnhandledExceptionHandlerComponent implements DistributionExceptionRaiser
{
	private Adapter adapter = new Adapter(this);
	private Throwable ex = null;

	public Adapter getAdapter()
	{
		return adapter;
	}

	public void addNotify()
	{
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler()
		{

			public void uncaughtException(Thread arg0, Throwable arg1)
			{
				ex = arg1;
				raise();
			}
		});
	}

	public void removeNotify()
	{
	}

	private void raise()
	{
		(new DistributionExceptionEvent(this)).raise();
	}

	public Event getEvent()
	{
		return null;
	}

	public EventController getTarget()
	{
		return null;
	}

	public Throwable getException()
	{
		return ex;
	}

}
