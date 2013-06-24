package app.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;

class MyLabel extends JLabel
{
	private static final long serialVersionUID = 1L;

	public MyLabel(String text)
	{
		super(text);
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		FontRenderContext frc = ((Graphics2D) g).getFontRenderContext();
		Rectangle2D r2d = g.getFont().getStringBounds(getText(), frc);
		g.setColor(new Color(0, 0, 128));
		g.drawLine(Math.max((int) r2d.getWidth() + 10, 63), this.getHeight() / 2, this.getWidth(), this.getHeight() / 2);
		g.setColor(Color.white);
		g.drawLine(Math.max((int) r2d.getWidth() + 10, 63), this.getHeight() / 2 + 1, this.getWidth(), this.getHeight() / 2 + 1);

	}

}
