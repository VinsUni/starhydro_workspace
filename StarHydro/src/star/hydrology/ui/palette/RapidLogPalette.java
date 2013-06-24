package star.hydrology.ui.palette;

import java.awt.Color;

public class RapidLogPalette implements Palette
{
	private Color c;
	private String name;

	public RapidLogPalette(Color c, String name)
	{
		super();
		this.c = c;
		this.name = name;
	}

	public Color getColor(float f)
	{
		float[] a = c.getColorComponents(new float[4]);
		a[3] = 1.0f;
		return new Color(get(a[0], f), get(a[1], f), get(a[2], f), a[3]);
	}

	float get(float a, float b)
	{
		float factor = 50;
		float ret = (float) (1 - Math.exp(-a * b * factor));
		return ret;
	}

	public String getName()
	{
		return name;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof RapidLogPalette)
		{
			RapidLogPalette that = (RapidLogPalette) obj;
			return that.c.equals(this.c) && that.name.equals(this.name);
		}
		return super.equals(obj);
	}

}
