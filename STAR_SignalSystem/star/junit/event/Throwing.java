package star.junit.event;

import junit.framework.TestCase;
import star.junit.event.throwing.Listener;
import star.junit.event.throwing.Raiser;

public class Throwing extends TestCase
{
	Raiser r;
	Listener l;

	protected void setUp() throws Exception
	{
		super.setUp();
		r = new Raiser();
		l = new Listener();
		r.getAdapter().addComponent(l);
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void test1()
	{
		String text = "Test";
		r.raise(text);
		assertEquals(text, text);
	}

	public void test2()
	{
		String text = "Another Test";
		r.raise(text);
		assertEquals(text, text);
	}

}
