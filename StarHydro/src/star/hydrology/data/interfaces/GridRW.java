package star.hydrology.data.interfaces;

public interface GridRW extends Grid
{
	void setSize(int cols, int rows);

	void setElementAt(int x, int y, float value);
}
