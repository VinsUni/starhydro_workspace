package mit.swing;

public class xJComboBox extends javax.swing.JComboBox implements mit.awt.event.ComponentRaiser, mit.awt.event.FocusRaiser, mit.awt.event.ItemRaiser, mit.awt.event.KeyRaiser, mit.awt.event.MouseRaiser
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

	public java.awt.event.ItemEvent getItemEvent()
	{
		return item.getItemEvent();
	}

	public java.awt.event.KeyEvent getKeyEvent()
	{
		return key.getKeyEvent();
	}

	public java.awt.event.MouseEvent getMouseEvent()
	{
		return mouse.getMouseEvent();
	}

	protected mit.awt.event.ComponentAdapter component = new mit.awt.event.ComponentAdapter(this);
	protected mit.awt.event.FocusAdapter focus = new mit.awt.event.FocusAdapter(this);
	protected mit.awt.event.ItemAdapter item = new mit.awt.event.ItemAdapter(this);
	protected mit.awt.event.KeyAdapter key = new mit.awt.event.KeyAdapter(this);
	protected mit.awt.event.MouseAdapter mouse = new mit.awt.event.MouseAdapter(this);

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