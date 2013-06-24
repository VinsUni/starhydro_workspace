package star.junit.event;

import javax.swing.JFrame;

import junit.framework.TestCase;
import star.junit.event.containment.Container;

public class Containment extends TestCase
{
	JFrame top;
	Container c;
	Container d;

	protected void setUp() throws Exception
	{
		super.setUp();
		top = new JFrame();
		c = new Container();
		d = new Container();
		top.getContentPane().add(c);
		top.getContentPane().add(d);
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

	public void test3()
	{
		String text = "Test";
		d.r.raise(text);
		assertEquals(text, c.c.getText());
	}

	public void test4()
	{
		String text = "Another Test";
		d.r.raise(text);
		assertEquals(text, c.c.getText());
	}

}
