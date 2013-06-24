package star.junit.event.eventraiser;

import star.event.Event;

public class TestEvent extends Event
{
	private static final long serialVersionUID = 1L;

	String text = null;

	public TestEvent(star.event.Event event)
	{
		super(event);
	}

	public TestEvent(TestRaiser source, boolean valid)
	{
		super(source,valid);
	}

	public TestEvent(TestRaiser source)
	{
		super(source);
	}

	/**
	 * @return Returns the text.
	 */
	public String getText()
	{
		return text;
	}

	/**
	 * @param text
	 *            The text to set.
	 */
	public void setText(String text)
	{
		this.text = text;
	}

}
