package star.hydrology.data.interfaces;

import java.awt.Color;

import star.hydrology.ui.palette.Palette;

public interface Grid
{
	int getRows();

	int getCols();

	float getElementAt(int x, int y);

	Color getColorAt(int x, int y, Palette p);

	void getPoint(int x, int y, float[] array);

	float getCellsize();

}
