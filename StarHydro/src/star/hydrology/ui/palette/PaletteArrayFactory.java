package star.hydrology.ui.palette;

import java.awt.Color;

public class PaletteArrayFactory
{
	public static float[] getArray(Palette palette, int distinct_colors)
	{
		float[] paletteColors = new float[distinct_colors * 4];
		if (palette != null)
		{
			for (int i = 0; i < distinct_colors; i++)
			{
				Color c = palette.getColor(1.0f * i / (distinct_colors - 1));
				paletteColors[i * 4 + 0] = c.getRed() * 1.0f / 255;
				paletteColors[i * 4 + 1] = c.getGreen() * 1.0f / 255;
				paletteColors[i * 4 + 2] = c.getBlue() * 1.0f / 255;
				paletteColors[i * 4 + 3] = c.getAlpha() * 1.0f / 255;
			}
		}
		return paletteColors;
	}
}
