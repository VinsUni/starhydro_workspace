package star.junit.event;

import javax.swing.JFrame;

import junit.framework.TestCase;
import star.junit.event.objobjobj.Container;

public class SignalObjObjObj extends TestCase
{
	JFrame top;
	Container c;

	protected void setUp() throws Exception
	{
		super.setUp();
		c = new Container();
		c.addNotify();
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void test1()
	{
		String text = "Test";
		c.r.raise(text);
		assertEquals(text, c.c.getText());
	}

	public void test2()
	{
		String text = "Another Test";
		c.r.raise(text);
		assertEquals(text, c.c.getText());
	}

}
