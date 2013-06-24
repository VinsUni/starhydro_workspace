package mit.awt.event;

public class ItemAdapter implements java.awt.event.ItemListener
{
	private java.awt.event.ItemEvent itemEvent = null;

	public java.awt.event.ItemEvent getItemEvent()
	{
		return this.itemEvent;
	}

	private mit.awt.event.ItemRaiser owner = null;

	public mit.awt.event.ItemRaiser getOwner()
	{
		return this.owner;
	}

	public ItemAdapter(mit.awt.event.ItemRaiser owner)
	{
		this.owner = owner;
		if( owner instanceof java.awt.Checkbox )
		{
			((java.awt.Checkbox) owner).addItemListener(this);
		}
		else if( owner instanceof java.awt.List )
		{
			((java.awt.List) owner).addItemListener(this);
		}
		else if( owner instanceof java.awt.Choice )
		{
			((java.awt.Choice) owner).addItemListener(this);
		}
		else if( owner instanceof java.awt.ItemSelectable )
		{
			((java.awt.ItemSelectable) owner).addItemListener(this);
		}
		else if( owner instanceof java.awt.CheckboxMenuItem )
		{
			((java.awt.CheckboxMenuItem) owner).addItemListener(this);
		}
		else if( owner instanceof javax.swing.AbstractButton )
		{
			((javax.swing.AbstractButton) owner).addItemListener(this);
		}
		else if( owner instanceof javax.swing.ButtonModel )
		{
			((javax.swing.ButtonModel) owner).addItemListener(this);
		}
		else if( owner instanceof javax.swing.JComboBox )
		{
			((javax.swing.JComboBox) owner).addItemListener(this);
		}
		else if( owner instanceof javax.swing.DefaultButtonModel )
		{
			((javax.swing.DefaultButtonModel) owner).addItemListener(this);
		}
	}

	public void itemStateChanged(java.awt.event.ItemEvent e)
	{
		setEvent(e);
	}

	private void setEvent(java.awt.event.ItemEvent e)
	{
		try
		{
			if( e.getSource().equals(this.getOwner()) )
			{
				this.itemEvent = e;
				new mit.awt.event.ItemEvent(this.getOwner()).raise();
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

}