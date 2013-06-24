package star.swing.events;

import star.annotations.Raiser;

@Raiser
public interface ProgressRaiser extends star.event.Raiser
{
	int getValue();

	String getType();

	String getString();
}
