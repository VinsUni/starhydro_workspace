package star.j3d.ui.signal;

import java.awt.Rectangle;

import javax.swing.JComponent;

import star.event.Raiser;

public interface DirtyRegionRaiser extends Raiser
{
	JComponent getDirtyComponent();

	Rectangle getDirtyRegion();
}
