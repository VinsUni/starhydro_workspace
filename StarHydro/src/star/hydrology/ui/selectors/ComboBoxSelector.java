package star.hydrology.ui.selectors;

import java.awt.FlowLayout;

import javax.swing.ComboBoxModel;
import javax.swing.SwingUtilities;

import mit.awt.event.ActionEvent;
import mit.awt.event.ItemEvent;
import mit.awt.event.ItemRaiser;
import mit.swing.xJComboBox;
import mit.swing.xJLabel;
import mit.swing.xJPanel;
import star.annotations.Handles;
import star.annotations.SignalComponent;
import star.hydrology.events.InitializeRaiser;

@SignalComponent(extend = xJPanel.class, contains = { ActionEvent.class, ItemEvent.class }, handles = { ItemEvent.class }, excludeExternal = { ActionEvent.class, ItemEvent.class })
public abstract class ComboBoxSelector extends ComboBoxSelector_generated
{
	static final long serialVersionUID = 1L;

	xJLabel textLabel;
	xJComboBox pulldown;

	private String text;
	private ComboBoxModel model;
	private boolean initialized = false;

	public void addNotify()
	{
		super.addNotify();
		setLayout(new FlowLayout());
		textLabel = new xJLabel();
		pulldown = new xJComboBox();
		add(textLabel);
		add(pulldown);
		updateView();
	}

	public void removeNotify()
	{
		super.removeNotify();
		textLabel = null;
		pulldown = null;
	}

	public void setText(String text)
	{
		this.text = text != null ? text : "";
		updateView();
	}

	public String getText()
	{
		return this.text;
	}

	public void setModel(ComboBoxModel model)
	{
		this.model = model;
		updateView();
	}

	public ComboBoxModel getModel()
	{
		return this.model;
	}

	void updateView()
	{
		if (textLabel != null && text != null)
		{
			textLabel.setText(text);
		}
		if (pulldown != null && model != null)
		{
			pulldown.setModel(model);
			if (initialized)
			{
				javax.swing.SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						raiseEvent(pulldown.getSelectedItem());
					}
				});
			}
		}
	}

	@Handles(raises = {})
	protected void handleEvent(InitializeRaiser raiser)
	{
		init();
	}

	protected void handleEvent(ItemRaiser raiser)
	{
		java.awt.event.ItemEvent e = raiser.getItemEvent();
		if (java.awt.event.ItemEvent.SELECTED == e.getStateChange())
		{
			raiseEvent(e.getItem());
		}
	}

	abstract void raiseEvent(Object item);

	void init()
	{
		initialized = true;
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				raiseEvent(pulldown.getSelectedItem());
			}
		});
	}

}
