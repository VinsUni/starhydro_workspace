package star.hydrology.ui.palette;

import java.awt.Color;

public class SingleColorPalette implements Palette
{
	private Color c;
	private String name;

	public SingleColorPalette(Color c, String name)
	{
		super();
		this.c = c;
		this.name = name;
	}

	public Color getColor(float f)
	{
		return c;
	}

	public String getName()
	{
		return name;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof SingleColorPalette && ((SingleColorPalette) obj).name.equals(this.name) && ((SingleColorPalette) obj).c.equals(this.c);
	}
}
