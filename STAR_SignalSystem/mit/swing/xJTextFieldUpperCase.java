package mit.swing;

public class xJTextFieldUpperCase extends javax.swing.JTextField implements mit.awt.event.ActionRaiser, mit.awt.event.ComponentRaiser, mit.awt.event.FocusRaiser, mit.awt.event.KeyRaiser, mit.awt.event.MouseRaiser, mit.swing.event.DocumentRaiser
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected javax.swing.text.Document createDefaultModel()
	{
		return new UpperCaseDocument();
	}

	static class UpperCaseDocument extends javax.swing.text.PlainDocument
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void insertString(int offs, String str, javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException
		{
			if( null != str )
			{
				char[] upper = str.toCharArray();
				for (int i = 0; upper.length != i; i++)
				{
					upper[i] = Character.toUpperCase(upper[i]);
				}
				super.insertString(offs, new String(upper), a);
			}
		}
	}

	private star.event.Adapter adapter = new star.event.Adapter(this);

	public star.event.Adapter getAdapter()
	{
		return this.adapter;
	}

	public java.awt.event.ActionEvent getActionEvent()
	{
		return action.getActionEvent();
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

	public javax.swing.event.DocumentEvent getDocumentEvent()
	{
		return document.getDocumentEvent();
	}

	protected mit.awt.event.ActionAdapter action = new mit.awt.event.ActionAdapter(this);
	protected mit.awt.event.ComponentAdapter component = new mit.awt.event.ComponentAdapter(this);
	protected mit.awt.event.FocusAdapter focus = new mit.awt.event.FocusAdapter(this);
	protected mit.awt.event.KeyAdapter key = new mit.awt.event.KeyAdapter(this);
	protected mit.awt.event.MouseAdapter mouse = new mit.awt.event.MouseAdapter(this);
	protected mit.swing.event.DocumentAdapter document = new mit.swing.event.DocumentAdapter(this);

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