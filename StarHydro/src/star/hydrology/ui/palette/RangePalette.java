package star.hydrology.ui.palette;

import java.awt.Color;

public class RangePalette implements Palette
{
	private Color from, to;
	private String name;

	public RangePalette(Color from, Color to, String name)
	{
		super();
		this.from = from;
		this.to = to;
		this.name = name;
	}

	public Color getColor(float f)
	{
		float[] a = to.getColorComponents(new float[4]);
		float[] b = from.getColorComponents(new float[4]);
		return new Color(get(a[0], b[0], f), get(a[1], b[1], f), get(a[2], b[2], f), 1.0f);
	}

	float get(float a, float b, float c)
	{
		return a * c + b * (1 - c);
	}

	public String getName()
	{
		return name;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof RangePalette)
		{
			RangePalette that = (RangePalette) obj;
			return that.from.equals(this.from) && that.to.equals(this.to) && that.name.equals(this.name);
		}
		return super.equals(obj);
	}

}