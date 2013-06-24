package star.hydrology.ui.palette;

import java.awt.Color;

public class StepPalette implements Palette
{
	private String name;

	public StepPalette(String name)
	{
		super();
		this.name = name;
	}

	public Color getColor(float f)
	{
		int index = (int) (f * 32);
		float fc = 0;
		float factor = .5f;
		while (index != 0)
		{
			if ((index & 1) == 1)
			{
				fc += factor;
			}
			index /= 2;
			factor /= 2;
		}
		return Color.getHSBColor(fc, 1, 1);
	}

	public String getName()
	{
		return name;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof StepPalette && ((StepPalette) obj).name.equals(this.name);
	}
}
