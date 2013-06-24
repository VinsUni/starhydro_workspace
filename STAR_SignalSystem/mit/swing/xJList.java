package mit.swing;

import javax.swing.ListModel;

public class xJList extends javax.swing.JList implements mit.awt.event.ComponentRaiser, mit.awt.event.FocusRaiser, mit.awt.event.KeyRaiser, mit.swing.event.ListSelectionRaiser, mit.awt.event.MouseRaiser
{
	private static final long serialVersionUID = 1L;
	private star.event.Adapter adapter = new star.event.Adapter(this);

	public star.event.Adapter getAdapter()
	{
		return this.adapter;
	}

	public java.awt.event.ComponentEvent getComponentEvent()
	{
		return component.getComponentEvent();
	}

	public java.awt.event.FocusEvent getFocusEvent()
	{
		return focus.getFocusEvent();
	}

	public java.awt.event.KeyEvent getKeyEvent()
	{
		return key.getKeyEvent();
	}

	public java.awt.event.MouseEvent getMouseEvent()
	{
		return mouse.getMouseEvent();
	}

	public javax.swing.event.ListSelectionEvent getListSelectionEvent()
	{
		return listSelection.getListSelectionEvent();
	}

	protected mit.awt.event.ComponentAdapter component = new mit.awt.event.ComponentAdapter(this);
	protected mit.awt.event.FocusAdapter focus = new mit.awt.event.FocusAdapter(this);
	protected mit.awt.event.KeyAdapter key = new mit.awt.event.KeyAdapter(this);
	protected mit.swing.event.ListSelectionAdapter listSelection = new mit.swing.event.ListSelectionAdapter(this);
	protected mit.awt.event.MouseAdapter mouse = new mit.awt.event.MouseAdapter(this);

	public xJList(ListModel model)
	{
		super(model);
	}

	public void addNotify()
	{
		super.addNotify();
		star.event.Event.invalidate();
	}

	public void removeNotify()
	{
		super.removeNotify();
		star.event.Event.invalidate();
	}

}