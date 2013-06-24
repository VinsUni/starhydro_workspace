package star.hydrology.ui.palette;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

public class PaletteCanvas extends JComponent
{
	private static final long serialVersionUID = 1L;

	private Palette p = null;

	public PaletteCanvas(Palette p)
	{
		super();
		this.p = p;
	}

	public Dimension getPreferredSize()
	{
		return new Dimension(60, 12);
	}

	public void paint(Graphics g)
	{
		Dimension d = getSize();
		int w = (int) d.getWidth();
		int h = (int) d.getHeight();
		if (w < 0)
		{
			w = 50;
		}
		for (int i = 1; i < w - 1; i++)
		{
			g.setColor(p.getColor(1.0f * (float) i / w));
			g.fillRect(i, 1, 1, h - 1);
		}
	}
}
