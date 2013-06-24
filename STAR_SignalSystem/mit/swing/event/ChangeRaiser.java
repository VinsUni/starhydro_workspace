package mit.swing.event;

public interface ChangeRaiser extends star.event.Raiser
{
	javax.swing.event.ChangeEvent getChangeEvent();
}