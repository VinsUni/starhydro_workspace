package star.ui.feedback;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import star.ui.Messages;
import utils.UIHelpers;

import com.oreilly.servlet.HttpMessage;

public class FeedbackDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	FeedbackDialog self = this;
	String feedbackURL = "http://starapp.mit.edu/cgi-bin/feedback/webfeedback.cgi"; //$NON-NLS-1$
	String[] reportTypes = new String[] { Messages.getString("FeedbackDialog.01"), Messages.getString("FeedbackDialog.2"), Messages.getString("FeedbackDialog.3"), Messages.getString("FeedbackDialog.4") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	String project;
	String build;
	JTextField yourName = new JTextField(30);
	JTextField yourEmail = new JTextField(30);
	JList reportType = new JList(reportTypes);
	JTextArea comment = new JTextArea(10, 30);
	JButton send = new JButton(Messages.getString("FeedbackDialog.5")); //$NON-NLS-1$
	JButton cancel = new JButton(Messages.getString("FeedbackDialog.6")); //$NON-NLS-1$
	JCheckBox subscribe = new JCheckBox();

	public FeedbackDialog(Frame owner, String project, String build)
	{
		super(owner, true);
		this.project = project;
		this.build = build;		
		comment.setWrapStyleWord(true);
		comment.setLineWrap(true);
		addComponentListener( new ComponentListener()
		{
			
			public void componentShown(ComponentEvent e)
			{
				// TODO Auto-generated method stub
				
			}
			
			public void componentResized(ComponentEvent e)
			{
				self.invalidate();
				self.doLayout();
				self.getContentPane().invalidate();
				self.getContentPane().doLayout();
			}
			
			public void componentMoved(ComponentEvent e)
			{
				// TODO Auto-generated method stub
				
			}
			
			public void componentHidden(ComponentEvent e)
			{
				// TODO Auto-generated method stub
				
			}
		});		
	}

	public void reportBug(Throwable t, String build )
	{
		reportType.setSelectedValue(reportTypes[0], true);
		StringBuilder sb = new StringBuilder();
		sb.append(Messages.getString("FeedbackDialog.7")); //$NON-NLS-1$
		sb.append(Messages.getString("FeedbackDialog.8"));		 //$NON-NLS-1$
		sb.append("\n" + Messages.getString("FeedbackDialog.10")); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("\n\n==============="); //$NON-NLS-1$
		sb.append("\n" + Messages.getString("FeedbackDialog.9") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		sb.append( Messages.getString("FeedbackDialog.14") + "\n" ); //$NON-NLS-1$ //$NON-NLS-2$
		try
		{
			sb.append("\n\t" + Messages.getString("FeedbackDialog.17") + build ) ; //$NON-NLS-1$ //$NON-NLS-2$
			sb.append( "\n\t" + Messages.getString("FeedbackDialog.19") + t.getClass() ) ; //$NON-NLS-1$ //$NON-NLS-2$
			sb.append( "\n\t" + Messages.getString("FeedbackDialog.21") + t.getLocalizedMessage() ) ; //$NON-NLS-1$ //$NON-NLS-2$
			sb.append( "\n\t" + Messages.getString("FeedbackDialog.23") ) ; //$NON-NLS-1$ //$NON-NLS-2$
			for( StackTraceElement e : t.getStackTrace() )
			{
				sb.append( "\n\t\t" + e.toString() ) ;  //$NON-NLS-1$
			}
			sb.append( "\n\t" + Messages.getString("FeedbackDialog.26") + t.getCause() ) ; //$NON-NLS-1$ //$NON-NLS-2$
			sb.append( "\n" ) ;  //$NON-NLS-1$
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			t.printStackTrace(new PrintStream(bos));
			bos.close();
			sb.append(bos.toString());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			sb.append( ex.getLocalizedMessage() ) ;
			sb.append( ex.getCause() ) ;
			if( ex.getStackTrace() != null )
			{
				sb.append( Arrays.toString(ex.getStackTrace()));
			}
		}
		comment.setText(sb.toString());
		comment.setCaretPosition(0);
	}

	@Override
	public void addNotify()
	{
		super.addNotify();
		setTitle(Messages.getString("FeedbackDialog.28") + project); //$NON-NLS-1$
		this.getContentPane().setLayout( new BorderLayout() ) ;		
		Container c = new JPanel();
		this.getContentPane().add( BorderLayout.CENTER , c ) ;
		MigLayout layout = new MigLayout((new LC()).wrapAfter(2).fillX().fillY()); 
		c.setLayout(layout);
		c.add(new JLabel(Messages.getString("FeedbackDialog.29") + project + "."), "center, span 2"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		c.add(new JLabel(Messages.getString("FeedbackDialog.32"))); //$NON-NLS-1$
		c.add(new JLabel(project), "wrap"); //$NON-NLS-1$
		c.add(new JLabel(Messages.getString("FeedbackDialog.34"))); //$NON-NLS-1$
		c.add(yourName, "wrap"); //$NON-NLS-1$
		c.add(new JLabel(Messages.getString("FeedbackDialog.36"))); //$NON-NLS-1$
		c.add(yourEmail, "wrap"); //$NON-NLS-1$
		c.add(new JLabel(Messages.getString("FeedbackDialog.38"))); //$NON-NLS-1$
		c.add(reportType, "wrap"); //$NON-NLS-1$
		c.add(new JLabel(Messages.getString("FeedbackDialog.40"))); //$NON-NLS-1$
		c.add( new JScrollPane( comment ), "growx, growy, wrap"); //$NON-NLS-1$
		c.add(new JLabel(Messages.getString("FeedbackDialog.42"))); //$NON-NLS-1$
		c.add(subscribe, "wrap"); //$NON-NLS-1$

		c.add(send, "right"); //$NON-NLS-1$
		c.add(cancel, "left"); //$NON-NLS-1$
		reportType.setBorder(BorderFactory.createEtchedBorder());
		comment.setBorder(BorderFactory.createEtchedBorder());
		reportType.setSelectedIndex(0);
		subscribe.setSelected(true);

		send.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					if( yourEmail.getText().length() == 0 )
					{
//						Update with StarOptionPane.  Talk to Ivan about placing SOP in Signal instead of the individual projects.
						int ret = JOptionPane.showConfirmDialog( self, Messages.getString("FeedbackDialog.0") + "\n" +Messages.getString("FeedbackDialog.1"),Messages.getString("FeedbackDialog.47") , JOptionPane.OK_CANCEL_OPTION); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						if( ret == JOptionPane.CANCEL_OPTION )
						{
							return ;
						}
					}
					send();
					dispose();
				}
				catch (Exception ex)
				{
					JOptionPane.showMessageDialog(self, Messages.getString("FeedbackDialog.48") + ex.getLocalizedMessage()); //$NON-NLS-1$
					dispose();
				}
			}
		});

		cancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});

	}
	
	@Override
	public void pack()
	{
	    super.pack();
		UIHelpers.centerOnParent(this);
	}

	void send() throws Exception
	{
		HttpMessage message;
		message = new HttpMessage(new URL(feedbackURL));
		Properties prop = new Properties();
		prop.put(Messages.getString("FeedbackDialog.49"), project); //$NON-NLS-1$
		prop.put(Messages.getString("FeedbackDialog.50"), Messages.getString("FeedbackDialog.51") + project + Messages.getString("FeedbackDialog.52") + build); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		prop.put(Messages.getString("FeedbackDialog.53"), String.valueOf(yourName.getText())); //$NON-NLS-1$
		prop.put(Messages.getString("FeedbackDialog.54"), String.valueOf(reportType.getSelectedValue())); //$NON-NLS-1$
		prop.put(Messages.getString("FeedbackDialog.55"), String.valueOf(yourEmail.getText())); //$NON-NLS-1$
		prop.put(Messages.getString("FeedbackDialog.56"), String.valueOf(comment.getText())); //$NON-NLS-1$
		prop.put(Messages.getString("FeedbackDialog.57"), String.valueOf(subscribe.isSelected())); //$NON-NLS-1$
		message.sendPostMessage(prop);
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame(Messages.getString("FeedbackDialog.58")); //$NON-NLS-1$
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		FeedbackDialog d = new FeedbackDialog(f, "Test Project", "20080101"); //$NON-NLS-1$ //$NON-NLS-2$
		f.pack();
		f.setVisible(true);
		d.pack();
		d.setVisible(true);
	}
}
