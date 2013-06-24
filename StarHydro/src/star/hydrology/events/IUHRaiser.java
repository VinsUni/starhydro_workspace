package star.hydrology.events;

import star.annotations.Raiser;

@Raiser
public interface IUHRaiser extends star.event.Raiser
{
	int getNumberOfBins();

	float getMinimum();

	float getMaximum();

	float getValueAt(float time);

	int getKind();

	int getNumberOfGauges();

	float getValueAt(int gauge, float time);
}
