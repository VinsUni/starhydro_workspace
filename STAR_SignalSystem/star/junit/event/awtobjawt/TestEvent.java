package star.junit.event.awtobjawt;

import star.event.Event;
import star.event.Raiser;

public class TestEvent extends Event
{
	private static final long serialVersionUID = 1L;

	String text = null;

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

	public TestEvent(Raiser source)
	{
		super(source);
	}

}
