package star.junit.event;

import junit.framework.TestCase;
import star.junit.event.gate.ComponentA;
import star.junit.event.gate.ComponentB;
import star.junit.event.gate.ComponentC;

public class SimpleGate extends TestCase
{

	public void test1()
	{
		ComponentA a = new ComponentA();
		ComponentB b = new ComponentB();
		ComponentC c = new ComponentC();
		ComponentA x = new ComponentA();
		x.getAdapter().addComponent(c);
		c.getAdapter().addComponent(b);
		c.getAdapter().addComponent(a);

		a.raise(4);
		assertEquals(4, a.getValue());
		assertEquals(4 * 4, b.getValue());
		assertNotNull(c.getData());
	}
}
