package mit.swing.event;

public class DocumentAdapter implements javax.swing.event.DocumentListener
{
	private javax.swing.event.DocumentEvent documentEvent = null;

	public javax.swing.event.DocumentEvent getDocumentEvent()
	{
		return this.documentEvent;
	}

	private mit.swing.event.DocumentRaiser owner = null;

	public mit.swing.event.DocumentRaiser getOwner()
	{
		return this.owner;
	}

	public DocumentAdapter(mit.swing.event.DocumentRaiser owner)
	{
		this.owner = owner;
		if( owner instanceof javax.swing.text.JTextComponent )
		{
			((javax.swing.text.JTextComponent) owner).getDocument().addDocumentListener(this);
		}
		else if( owner instanceof javax.swing.text.View )
		{
			((javax.swing.text.View) owner).getDocument().addDocumentListener(this);
		}
		else if( owner instanceof javax.swing.text.Element )
		{
			((javax.swing.text.Element) owner).getDocument().addDocumentListener(this);
		}
		else if( owner instanceof javax.swing.text.AbstractDocument.AbstractElement )
		{
			((javax.swing.text.AbstractDocument.AbstractElement) owner).getDocument().addDocumentListener(this);
		}
	}

	public void changedUpdate(javax.swing.event.DocumentEvent e)
	{
		setEvent(e);
	}

	public void insertUpdate(javax.swing.event.DocumentEvent e)
	{
		setEvent(e);
	}

	public void removeUpdate(javax.swing.event.DocumentEvent e)
	{
		setEvent(e);
	}

	private void setEvent(javax.swing.event.DocumentEvent e)
	{
		try
		{
			javax.swing.text.JTextComponent tc = (javax.swing.text.JTextComponent) this.getOwner();
			if( e.getDocument().equals(tc.getDocument()) )
			{
				this.documentEvent = e;
				new mit.swing.event.DocumentEvent(this.getOwner()).raise();
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

}