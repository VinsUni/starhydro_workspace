package mit.awt.event;

public interface ComponentRaiser extends star.event.Raiser
{
	java.awt.event.ComponentEvent getComponentEvent();
}