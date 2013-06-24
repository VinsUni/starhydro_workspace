package star.j3d.ui;

import java.awt.Dimension;

import javax.media.j3d.J3DGraphics2D;

public interface PaintHook
{
	public void paint(J3DGraphics2D gr, Dimension size);
}
