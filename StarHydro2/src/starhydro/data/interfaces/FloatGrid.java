package starhydro.data.interfaces;

public interface FloatGrid
{
	float get(int x, int y);

	float[] get3x3(int x, int y);

	FloatRange getValueRange();

	// public void execute(final FloatGridVisitor statistics);
	// public void execute(final FloatGridStatistics statistics);
}
