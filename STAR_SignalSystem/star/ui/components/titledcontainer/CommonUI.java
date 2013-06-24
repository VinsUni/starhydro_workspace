package star.ui.components.titledcontainer;

import java.awt.Font;

import javax.swing.UIManager;

public class CommonUI
{

	public static CommonUI get()
	{
		return new CommonUI();
	}

	public java.awt.Color getPanelBackground()
	{
		java.awt.Color c = UIManager.getColor("Panel.background");
		if (c == null)
		{
			c = java.awt.Color.white;
		}
		return c;
	}

	public java.awt.Color getTitleBackground()
	{
		java.awt.Color c = UIManager.getColor("nimbusSelectionBackground");
		if (c == null)
		{
			c = java.awt.Color.lightGray.darker();
		}
		return c;
	}

	public java.awt.Color getTitleForeground()
	{
		java.awt.Color c = UIManager.getColor("nimbusSelectedText");
		if (c == null)
		{
			c = java.awt.Color.white;
		}
		return c;
	}

	public java.awt.Font getTitleFont(java.awt.Font font)
	{
		if (font == null)
		{
			font = UIManager.getFont("Label.font");
		}
		if (font == null)
		{
			font = new java.awt.Font("verdana", Font.PLAIN, 12);
		}
		font = font.deriveFont(14f).deriveFont(Font.BOLD);
		return font;
	}

	public java.awt.Font getLightFont(java.awt.Font font)
	{
		if (font == null)
		{
			font = UIManager.getFont("Label.font");
		}
		if (font == null)
		{
			font = new java.awt.Font("verdana", Font.PLAIN, 12);
		}
		font = font.deriveFont(12f).deriveFont(Font.ITALIC);
		return font;
	}

	private static java.awt.Font smallFont = new java.awt.Font(Font.SANS_SERIF, Font.PLAIN, 9);

	public Font getSmallFont()
	{
		return smallFont;
	}
}
