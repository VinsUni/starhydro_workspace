package utils;

import javax.swing.SwingUtilities;

import star.event.Event;

public class EventRaiser
{
	public static void raiseSwing(final Event e)
	{
		SwingUtilities.invokeLater(new Runnable()
		{

			public void run()
			{
				e.raise();
			}
		});
	}

	public static void raiseSync(final Event e)
	{
		e.raise();
	}

	public static void raiseAsync(final Event e)
	{
		new Thread()
		{
			public void run()
			{
				e.raise();
			}
		}.start();
	}

}
