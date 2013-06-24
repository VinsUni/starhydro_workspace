package star.annotations;

public @interface Raiser
{
	final static int ASYNC = 1;
	final static int SYNC = 2;
	final static int POOLED = 3;
	final static int SWING = 4;
	final static int NONREENTERABLE = 8;

	public int concurrency() default SYNC;
}
