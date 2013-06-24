package star.annotations;

public @interface Handles
{
	final static int ASYNC = 1;
	final static int SYNC = 2;
	final static int POOLED = 3;
	final static int SWING = 4;
	final static int NONREENTERABLE = 8;

	public Class[] raises();

	public int concurrency() default SYNC;

	public boolean handleValid() default true;
}
