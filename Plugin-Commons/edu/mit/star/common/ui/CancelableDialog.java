package edu.mit.star.common.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class CancelableDialog extends JDialog
{
	private static final String CANCEL = "Cancel"; //$NON-NLS-1$
	private static final long serialVersionUID = 1L;
	String text = null;
	Runnable process = null;
	Thread thread = null;

	public CancelableDialog(Dialog dialog)
	{
		super(dialog);
	}

	public CancelableDialog(Frame dialog)
	{
		super(dialog);
	}

	public CancelableDialog()
	{
		super();
	}

	public void addNotify()
	{
		super.addNotify();
		Container panel = getContentPane();
		panel.setLayout(new BorderLayout());
		JLabel label = new JLabel(text);
		label.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.add(BorderLayout.CENTER, label);
		JButton cancel = new JButton(CANCEL);
		panel.add(BorderLayout.SOUTH, cancel);

		cancel.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				thread.interrupt();
				setVisible(false);
				dispose();
			}
		});
	}

	public void start()
	{
		thread = new Thread(new Runnable()
		{
			public void run()
			{
				synchronized (process)
				{
					if (process != null)
					{
						process.run();
						process = null;
					}
				}
				SwingUtilities.invokeLater(new Runnable()
				{

					public void run()
					{
						setVisible(false);
					}
				});
			}
		});
		thread.start();

		if (process != null)
		{
			setVisible(true);
		}
	}

	public static void getCancelableDialog(Component parent, String title, String text, final Runnable process)
	{
		CancelableDialog ret = null;
		while (parent != null && parent != parent.getParent())
		{
			if (parent instanceof Dialog)
			{
				ret = new CancelableDialog((Dialog) parent);
				ret.setLocation(parent.getX() + parent.getWidth() / 2, parent.getY() + parent.getHeight() / 2);
				break;
			}
			if (parent instanceof Frame)
			{
				ret = new CancelableDialog((Frame) parent);
				ret.setLocation(parent.getX() + parent.getWidth() / 2, parent.getY() + parent.getHeight() / 2);
				break;
			}
			parent = parent.getParent();
		}
		if (ret == null)
		{
			ret = new CancelableDialog();
		}
		if (title != null)
		{
			ret.setTitle(title);
		}
		ret.setModal(true);
		ret.text = text;
		ret.process = process;
		ret.pack();
		ret.start();
		// ret.setVisible( true ) ;
	}

	static Thread initUI(final JDialog dialog, Container panel, String text, final Runnable process)
	{
		final Thread t = new Thread(new Runnable()
		{
			public void run()
			{
				dialog.setVisible(true);
				process.run();
				dialog.setVisible(false);
				dialog.dispose();
			}
		});

		return t;
	}
}
