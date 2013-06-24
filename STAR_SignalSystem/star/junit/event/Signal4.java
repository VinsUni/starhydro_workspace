package star.junit.event;

import junit.framework.TestCase;
import star.junit.event.objobjobj.Raiser;

public class Signal4 extends TestCase
{
	Raiser r;

	protected void setUp() throws Exception
	{
		super.setUp();
		r = new Raiser();
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
