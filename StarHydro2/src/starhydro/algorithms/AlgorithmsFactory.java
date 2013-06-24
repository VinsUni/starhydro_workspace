package starhydro.algorithms;

public class AlgorithmsFactory
{
	public PitFillingAlgorithm getPitFilledAlgorithm()
	{
		return new PitFillingAlgorithm();
	}
	
	public FlowDirectionAlgorithm getFlowDirectionAlgorithm()
	{
		return new FlowDirectionAlgorithm();
	}
	
	public FlowAccumulationAlgorithm getFlowAccumulationAlgorithm()
	{
		return new FlowAccumulationAlgorithm();
	}

	public DelineateWatershed getDelineateWatershedAlgorithm()
	{
		return new DelineateWatershed();
	}
}
