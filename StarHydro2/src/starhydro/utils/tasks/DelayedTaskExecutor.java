package starhydro.utils.tasks;

import java.util.ArrayList;
import java.util.Iterator;

public class DelayedTaskExecutor implements Runnable
{
	private static DelayedTaskExecutor executor = new DelayedTaskExecutor();

	public static DelayedTaskExecutor getDelayedTaskExecutor()
	{
		return executor;
	}

	class TaskWrapper
	{
		Runnable t;
		long time;
	}

	private Object lock = new Object();
	private ArrayList<TaskWrapper> queue = new ArrayList<TaskWrapper>();
	private Thread t;
	private boolean canceled = false;

	private DelayedTaskExecutor()
	{
		t = new Thread(this);
		t.setDaemon(true);
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
	}

	public void cancel(Runnable task)
	{
		synchronized( lock )
		{
			Iterator<TaskWrapper> iter = queue.iterator();
			while( iter.hasNext() )
			{
				TaskWrapper t = iter.next();
				if( t.time < System.currentTimeMillis() )
				{
					iter.remove();
					t.t.run();
				}
			}
			lock.notifyAll();
		}
	}

	public void schedule(Runnable task, long delay)
	{
		synchronized( lock )
		{
			boolean contains = false;
			for (TaskWrapper t : queue)
			{
				if( t.t.equals(task) )
				{
					t.time = System.currentTimeMillis() + delay;
					contains = true;
					break;
				}
			}
			if( !contains )
			{
				TaskWrapper wrapper = new TaskWrapper();
				wrapper.t = task;
				wrapper.time = System.currentTimeMillis() + delay;
				queue.add(wrapper);
			}
			lock.notifyAll();
		}
	}

	public void run()
	{
		while( !canceled )
		{
			while( queue.size() != 0 )
			{
				synchronized( lock )
				{
					Iterator<TaskWrapper> iter = queue.iterator();
					while( iter.hasNext() )
					{
						TaskWrapper t = iter.next();
						if( t.time <= System.currentTimeMillis() )
						{
							iter.remove();
							t.t.run();
						}
					}
				}
			}
			synchronized( lock )
			{
				try
				{
					lock.wait();
				}
				catch( Exception ex )
				{
					ex.printStackTrace();
				}
			}
			// utils.Runner.sleep(100);
		}
	}

}
