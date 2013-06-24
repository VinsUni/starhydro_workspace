package utils;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Window;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import star.annotations.Handles;

public class Runner
{
	private static HashMap<Object, Thread> threadPool = new HashMap<Object, Thread>(50);
	private static int threadsStarted = 0;
	static boolean runOnThreadEnabled = true;
	static JLabel label = new JLabel("Status bar");
	static int counter = 0 ;

	public static String getActiveJobs()
	{
		return "" + (threadPool.size() != 0 ? "Running " + threadPool.size() + " tasks ; Finished: " + threadsStarted : "" );
	}

	public static JLabel getLabel()
	{
		return label;
	}

	static int lastPool = Integer.MAX_VALUE;
	static int lastStarted = Integer.MAX_VALUE;

	static void updateLabel()
	{
		if( lastPool != threadPool.size() || lastStarted != threadsStarted )
		{
			lastPool = threadPool.size();
			lastStarted = threadsStarted;

			getLabel().setText(getActiveJobs());
			getLabel().invalidate();
			updateCursor(getLabel());
		}
	}

	static void updateCursor(Component c)
	{
		if( c instanceof Window )
		{
			if( threadPool.size() != 0 )
			{
				c.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			}
			else
			{
				c.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
		else if( c != null )
		{
			updateCursor(c.getParent());
		}
	}

	
	public static void runOnThread(final Runnable r, final Object self, final int concurrency)
	{
		updateLabel();
		if( concurrency == Handles.SYNC )
		{
			r.run();
		}
		else
		// if(concurrency==Handles.ASYNC)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					synchronized( threadPool )
					{
						if( threadPool.size() > Runtime.getRuntime().availableProcessors() )
						{
							counter++;
							//System.out.println( "Runner: threads:" + threadPool.size() + " cpus:" + Runtime.getRuntime().availableProcessors() + " times:" + counter + " " + self.getClass().toString() + " finished: " + threadsStarted ) ;
						}
					}
					Thread t = null;
					synchronized( threadPool )
					{
						t = threadPool.get(self);
					}
					if( t != null && t.isAlive() )
					{
						t.interrupt();
						runOnThread(r, self, concurrency);
					}
					else
					{
						synchronized( threadPool )
						{
							t = new Thread()
							{
								public void run()
								{
									r.run();
									synchronized( threadPool )
									{
										if( threadPool.get(self) == currentThread() )
										{
											threadPool.remove(self);
											updateLabel();
										}
									}
								}
							};
							threadsStarted++;
							t.start();
							threadPool.put(self, t);
						}
						updateLabel();
					}
				}
			});
		}
	}

	public static void interruptThread(Object self)
	{
		Thread t = null;
		synchronized( threadPool )
		{
			t = threadPool.get(self);
		}
		if( t != null )
		{
			if( t.isAlive() )
			{
				t.interrupt();
				try
				{
					t.interrupt();
					t.join(100);
				}
				catch( InterruptedException e )
				{
					e.printStackTrace();
				}
			}
			if( !t.isAlive() )
			{
				t = null;
			}
		}
	}

	public static boolean isInterrupted()
	{
		return Thread.currentThread().isInterrupted();
	}

	public static boolean sleep(int time)
	{
		boolean ret = false;
		try
		{
			Thread.sleep(time);
		}
		catch( InterruptedException e )
		{
			ret = true;
		}
		return ret;
	}

	public static long start()
	{
		return System.nanoTime() ;
	}

	public static long stop( long start , String message )
	{
		long stop = System.nanoTime() ;
		if( message != null )
		{
			System.out.println( message + " " + (stop-start)/1000000f + " ms") ;
		}
		return stop - start ;
	}

}
