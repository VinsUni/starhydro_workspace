package starhydro.view2d;

public interface Palette
{
	public void setRange(float min, float max);

	public java.awt.Color getColor(float value);
	
	public long getTimestamp() ;
}
