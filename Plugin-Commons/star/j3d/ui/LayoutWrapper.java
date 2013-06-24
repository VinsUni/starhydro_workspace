package star.j3d.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Point;

public class LayoutWrapper implements LayoutManager2, LayoutHelper
{

	LayoutManager layoutManager;
	Container3D container;

	public LayoutWrapper(LayoutManager layoutManager, Container3D container)
	{
		this.layoutManager = layoutManager;
		this.container = container;
	}

	public void addLayoutComponent(Component comp, Object constraints)
	{
		if (layoutManager instanceof LayoutManager2)
		{
			((LayoutManager2) this.layoutManager).addLayoutComponent(comp, constraints);
		}
	}

	public float getLayoutAlignmentX(Container target)
	{
		float ret = 0;
		if (layoutManager instanceof LayoutManager2)
		{
			ret = ((LayoutManager2) this.layoutManager).getLayoutAlignmentX(target);
		}
		return ret;
	}

	public float getLayoutAlignmentY(Container target)
	{
		float ret = 0;
		if (layoutManager instanceof LayoutManager2)
		{
			ret = ((LayoutManager2) this.layoutManager).getLayoutAlignmentY(target);
		}
		return ret;
	}

	public void invalidateLayout(Container target)
	{
		if (layoutManager instanceof LayoutManager2)
		{
			((LayoutManager2) this.layoutManager).invalidateLayout(target);
		}
	}

	public Dimension maximumLayoutSize(Container target)
	{
		Dimension ret = null;
		if (layoutManager instanceof LayoutManager2)
		{
			ret = ((LayoutManager2) this.layoutManager).maximumLayoutSize(target);
		}
		return ret;
	}

	public void addLayoutComponent(String name, Component comp)
	{
		container.setInLayout(true);
		try
		{
			layoutManager.addLayoutComponent(name, comp);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		container.setInLayout(false);
	}

	public void layoutContainer(Container parent)
	{
		container.setInLayout(true);
		try
		{
			layoutManager.layoutContainer(parent);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		container.setInLayout(false);
	}

	public Dimension minimumLayoutSize(Container parent)
	{
		return layoutManager.minimumLayoutSize(parent);
	}

	public Dimension preferredLayoutSize(Container parent)
	{
		return layoutManager.preferredLayoutSize(parent);
	}

	public void removeLayoutComponent(Component comp)
	{
		layoutManager.removeLayoutComponent(comp);
	}

	public int getLayoutAreas(Point p)
	{
		if (layoutManager instanceof LayoutHelper)
		{
			return ((LayoutHelper) layoutManager).getLayoutAreas(p);
		}
		else
		{
			int ret = 0;
			container.setInLayout(true);
			int count = container.getComponentCount();
			for (int i = 0; i < count; i++)
			{
				if (container.getComponent(i).getBounds().contains(p))
				{
					ret = i + 1;
					break;
				}
			}
			container.setInLayout(false);
			return ret;
		}
	}

	public boolean isComponentInRectangle(Component component, int areas)
	{
		if (layoutManager instanceof LayoutHelper)
		{
			return ((LayoutHelper) layoutManager).isComponentInRectangle(component, areas);
		}
		else
		{
			try
			{
				if (container.getAllOrNone())
				{
					return areas != 0;
				}
				else
				{
					return container.getComponent(areas - 1) == component;
				}
			}
			catch (Exception ex)
			{
				return false;
			}
		}
	}
}
