package starhydro.view2d;

import java.awt.Color;

import utils.MathHelpers;

public class SimplePalette implements Palette
{
	float min;
	float max;
	Color color;
	float[] colorComponents = new float[4];
	long timestamp ;

	public SimplePalette(Color c)
	{
		this.color = c;
		color.getRGBComponents(colorComponents);
		System.out.println( "Simple Palette " + colorComponents[3] );
	}

	public Color getColor(float value)
	{
		float scale = MathHelpers.normalize((value - min) / (max - min));
		return new Color(colorComponents[0] * scale, colorComponents[1] * scale, colorComponents[2] * scale, colorComponents[3]);
	}

	public void setRange(float min, float max)
	{
		this.max = max;
		this.min = min;
		this.timestamp = System.nanoTime() ;
	}

	public long getTimestamp()
	{
		return timestamp ;
	}
}
