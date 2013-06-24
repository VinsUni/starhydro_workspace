package utils;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.MessageFormat;

import javax.swing.JProgressBar;

public class Memory extends JProgressBar implements Runnable
{
    private static final long serialVersionUID = 1L;
	public boolean stdout = false ;
	public int delay = 800 ;
	private static MemoryMXBean bean;
	Thread t;
	boolean stop = false ;

	void setDelay( int delay )
	{
		this.delay = delay ;
	}
	
	void setPrintToStdOut( boolean print )
	{
		this.stdout = print ; 
	}
	
	public Memory(int orient)
	{
		super(orient);
	}

	@Override
	public void addNotify()
	{
		super.addNotify();
		start();
	}

	@Override
	public void removeNotify()
	{
		super.removeNotify();
		stop();
	}

	private void start()
	{
		t = new Thread(this);
		t.setDaemon(true);
		stop = false ;
		t.start();
	}

	private void stop()
	{
		t.interrupt();
		t = null;
		stop = true;
	}

	public void run()
	{
		while( !stop )
		{
			Runner.sleep(delay);
			MemoryUsage heapUsage = getBean().getHeapMemoryUsage();
			long max = heapUsage.getMax();
			long used = heapUsage.getUsed();
			max /= 1024 * 1024;
			used /= 1024 * 1024;
			setMinimum(0);
			setMaximum((int) max);
			setValue((int) used);
			setStringPainted(true);
			setString(MessageFormat.format("Used {0} Mb Available {1} Mb", used,getAvailable()/1024/1024));
			if( stdout) { System.out.println(MessageFormat.format("{0} Mb", used)); }			
		}
	}
	
	static MemoryMXBean getBean()
	{
		if( bean == null )
		{ 
			bean = ManagementFactory.getMemoryMXBean();			
		}
		return bean ;
	}

	public static long getUsed()
	{
		MemoryUsage heapUsage = getBean().getHeapMemoryUsage();
		return heapUsage.getUsed();
	}
	
	public static long getAvailable()
	{
		return Runtime.getRuntime().freeMemory();
	}

	public static long getMax()
	{
		MemoryUsage heapUsage = getBean().getHeapMemoryUsage();
		return heapUsage.getMax();
	}
}
