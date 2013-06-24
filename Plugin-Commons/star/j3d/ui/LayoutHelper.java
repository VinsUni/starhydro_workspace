package star.j3d.ui;

import java.awt.Component;
import java.awt.Point;

public interface LayoutHelper
{
	public int getLayoutAreas(Point p);

	public boolean isComponentInRectangle(Component component, int areas);
}
