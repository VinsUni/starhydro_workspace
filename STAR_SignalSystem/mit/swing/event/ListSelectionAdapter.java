package mit.swing.event;

public class ListSelectionAdapter implements javax.swing.event.ListSelectionListener
{
	private javax.swing.event.ListSelectionEvent listSelectionEvent = null;

	public javax.swing.event.ListSelectionEvent getListSelectionEvent()
	{
		return this.listSelectionEvent;
	}

	private mit.swing.event.ListSelectionRaiser owner = null;

	public mit.swing.event.ListSelectionRaiser getOwner()
	{
		return this.owner;
	}

	public ListSelectionAdapter(mit.swing.event.ListSelectionRaiser owner)
	{
		this.owner = owner;
		if( owner instanceof javax.swing.DefaultListSelectionModel )
		{
			((javax.swing.DefaultListSelectionModel) owner).addListSelectionListener(this);
		}
		else if( owner instanceof javax.swing.JList )
		{
			((javax.swing.JList) owner).addListSelectionListener(this);
		}
		else if( owner instanceof javax.swing.ListSelectionModel )
		{
			((javax.swing.ListSelectionModel) owner).addListSelectionListener(this);
		}
		else if( owner instanceof javax.swing.JTable )
		{
			((javax.swing.JTable) owner).getSelectionModel().addListSelectionListener(this);
		}
	}

	public void valueChanged(javax.swing.event.ListSelectionEvent e)
	{
		setEvent(e);
	}

	private void setEvent(javax.swing.event.ListSelectionEvent e)
	{
		try
		{
			if( e.getSource().equals(this.getOwner()) )
			{
				this.listSelectionEvent = e;
				new mit.swing.event.ListSelectionEvent(this.getOwner()).raise();
			}
			else if( owner instanceof javax.swing.JTable )
			{
				// special case for JTables which manage all events through
				// their models
				javax.swing.ListSelectionModel model = ((javax.swing.JTable) (this.getOwner())).getSelectionModel();
				if( e.getSource().equals(model) )
				{
					this.listSelectionEvent = e;
					new mit.swing.event.ListSelectionEvent(this.getOwner()).raise();
				}
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

}