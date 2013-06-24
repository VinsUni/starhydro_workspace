package app;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingworker.SwingWorker.StateValue;

import star.annotations.Handles;
import star.annotations.SignalComponent;
import starhydro.events.BackgroundProcessRaiser;
import utils.UIHelpers;

@SignalComponent(extend = JPanel.class)
public class SwingRunnerPanel extends SwingRunnerPanel_generated
{

    private static final long serialVersionUID = 1L;

	public void addNotify()
	{
		super.addNotify();
		setPreferredSize(new Dimension( 300 , 200 ) ) ;
		setMinimumSize(new Dimension( 300 , 200 ) ) ;
		setBorder( BorderFactory.createEtchedBorder()) ;
		//setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add( new JLabel( "Swing Runner Panel " ) );
	}

	@Handles(raises={})
	protected void handle(BackgroundProcessRaiser raiser)
	{
		SwingWorker worker = raiser.getSwingWorker();
		final SwingWorkerWrapper wrapper = new SwingWorkerWrapper(worker,raiser.getName());
		if( SwingUtilities.isEventDispatchThread() )
		{
			add(wrapper);
		}
		else
		{
			SwingUtilities.invokeLater(new Runnable()
			{

				public void run()
				{
					add(wrapper);repaint();
				}
			});
		}
		final JPanel self = this ;
		worker.addPropertyChangeListener(new PropertyChangeListener()
		{

			public void propertyChange(PropertyChangeEvent evt)
            {
				if( "state".equals(evt.getPropertyName() ) )
				{
					if( evt.getNewValue() == StateValue.DONE )
					{
						if( SwingUtilities.isEventDispatchThread() )
						{
							remove(wrapper);
							UIHelpers.repaintTop(self);
						}
						else
						{
							SwingUtilities.invokeLater(new Runnable()
							{

								public void run()
								{
									remove(wrapper);
									UIHelpers.repaintTop(self);									
								}
							});
						}
					}
				}
	            
            }
		});
		worker.execute();

	}

	class SwingWorkerWrapper extends JPanel
	{
        private static final long serialVersionUID = 1L;
        
		SwingWorker worker;
		String label ;

		public SwingWorkerWrapper(SwingWorker worker, String label)
		{
			this.worker = worker;
			this.label = label ;
		}

		@Override
		public void addNotify()
		{
			super.addNotify();
			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			if( worker.getState() != StateValue.DONE )
			{
				final JProgressBar progressBar = new JProgressBar();
				worker.addPropertyChangeListener(new PropertyChangeListener()
				{
					public void propertyChange(PropertyChangeEvent evt)
					{
						if( "progress".equals(evt.getPropertyName()) )
						{
							progressBar.setValue((Integer) evt.getNewValue());
						}
					}
				});
				final JButton cancel = new JButton("Cancel");
				cancel.addActionListener(new ActionListener()
				{

					public void actionPerformed(ActionEvent e)
					{
						worker.cancel(true);
					}
				});
				add( new JLabel( label != null ? label : "Task:" ) );
				add( progressBar ) ;
				progressBar.setStringPainted(true);
				add( cancel ) ;
				getParent().validate();
				
			}

		}

	}

}
