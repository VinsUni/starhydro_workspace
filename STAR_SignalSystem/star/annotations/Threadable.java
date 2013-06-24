package star.annotations;

public interface Threadable
{
	public void runOnThread(Runnable r);

	public void interruptThread();
}
