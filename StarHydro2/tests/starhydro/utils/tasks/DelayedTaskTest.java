package starhydro.utils.tasks;

import org.junit.Test;

public class DelayedTaskTest
{
	@Test
	public void basicTest()
	{
		DelayedTaskExecutor exec = DelayedTaskExecutor.getDelayedTaskExecutor();
		// utils.Runner.sleep(200);
		exec.schedule(new Runnable()
		{
			public void run()
			{
				System.out.println("Running task");
			}
		}, 200);
		utils.Runner.sleep(2500);
	}

}
