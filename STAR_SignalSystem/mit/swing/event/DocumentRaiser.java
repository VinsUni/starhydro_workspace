package mit.swing.event;

public interface DocumentRaiser extends star.event.Raiser
{
	javax.swing.event.DocumentEvent getDocumentEvent();
}