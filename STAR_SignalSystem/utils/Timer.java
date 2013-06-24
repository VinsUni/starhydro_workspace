package utils;

import java.text.MessageFormat;
import java.util.Arrays;

public class Timer
{
	static long start ;
	
	public static void start()
	{
		start = System.nanoTime() ;
	}
	
	public static void stop()
	{
		long stop = System.nanoTime() ;
		RuntimeException ex = new RuntimeException();
		ex.fillInStackTrace();
		StackTraceElement[] elements = ex.getStackTrace();
		if( elements.length > 2 )
		{
			System.err.println( MessageFormat.format("Timer: {0} {1,Number,###.###} ms",elements[1] ,(stop-start)/1000000.0 )) ;
		}
		else
		{
			System.err.println( "Timer: " + Arrays.toString(elements) + " " + (stop-start)/1000.0 + "microsec" ) ;
		}
	}
}
