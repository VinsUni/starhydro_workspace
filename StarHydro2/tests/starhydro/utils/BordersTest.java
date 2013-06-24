package starhydro.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BordersTest
{

	@Test
	public void onEdges()
	{
		int x = 2;
		int y = 3;
		int w = 4;
		int h = 5;
		Rectangle2DInteger r = new Rectangle2DInteger(x, y, w, h);
		assertTrue("UpperLeft ", (new Point2DInteger(x, y)).borders(r));
		assertTrue("UpperRight ", (new Point2DInteger(x + w - 1, y)).borders(r));
		assertTrue("LowerLeft ", (new Point2DInteger(x, y + h - 1)).borders(r));
		assertTrue("LowerRight ", (new Point2DInteger(x + w - 1, y + h - 1)).borders(r));
	}

	@Test
	public void onEdgesWithin()
	{
		int x = 2;
		int y = 3;
		int w = 4;
		int h = 5;
		Rectangle2DInteger r = new Rectangle2DInteger(x, y, w, h);

		assertTrue("On top ", (new Point2DInteger(x + w / 2, y)).borders(r));
		assertTrue("On bottom ", (new Point2DInteger(x + w / 2, y + h)).borders(r));
		assertTrue("On left ", (new Point2DInteger(x, y + h / 2)).borders(r));
		assertTrue("On right ", (new Point2DInteger(x + w, y + h / 2)).borders(r));
		assertFalse("Within", (new Point2DInteger(x + 1, y + 1)).borders(r));
		assertFalse("Outside", (new Point2DInteger(x - 1, y - 1)).borders(r));
		assertFalse("Outside on top ", (new Point2DInteger(x - 1, y)).borders(r));
		assertFalse("Outside on top ", (new Point2DInteger(x + w + 1, y)).borders(r));
		assertFalse("Outside on bottom ", (new Point2DInteger(x - 1, y + h)).borders(r));
		assertFalse("Outside on bottom ", (new Point2DInteger(x + w + 1, y + h)).borders(r));
		assertFalse("Outside on left ", (new Point2DInteger(x, y - 1)).borders(r));
		assertFalse("Outside on left ", (new Point2DInteger(x, y + h + 1)).borders(r));
		assertFalse("Outside on right ", (new Point2DInteger(x + w, y - 1)).borders(r));
		assertFalse("Outside on right ", (new Point2DInteger(x + w, y + h + 1)).borders(r));

	}

}
