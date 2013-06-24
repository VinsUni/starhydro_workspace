package star.hydrology.ui.palette;

import java.awt.Color;

public class WheelPalette implements Palette
{
	private float s = 1;
	private float b = 1;
	private String name;

	public WheelPalette(String name, float s, float b)
	{
		this.s = s;
		this.b = b;
		this.name = name;
	}

	public Color getColor(float h)
	{
		return Color.getHSBColor(h * .8f, s, b);
	}

	public String getName()
	{
		return name;
	}

}
