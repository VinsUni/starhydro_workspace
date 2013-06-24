package star.junit.event;

import javax.swing.JFrame;

import junit.framework.TestCase;
import star.junit.event.awtawtawt.Container;

public class SwingAwtAwtAwt extends TestCase
{
	JFrame top;
	Container c;

	protected void setUp() throws Exception
	{
		super.setUp();
		top = new JFrame();
		c = new Container();
		top.getContentPane().add(c);
		top.pack();
		top.setVisible(true);
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
		top.setVisible(false);
		top.dispose();
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
