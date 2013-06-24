package app.layers;

import javax.swing.ComboBoxModel;

import star.annotations.Handles;
import star.annotations.Preferences;
import star.annotations.SignalComponent;
import star.event.Listener;
import star.hydrology.events.PaletteChangedEvent;
import star.hydrology.events.PaletteChangedRaiser;
import star.hydrology.events.ViewerChangeRaiser;
import star.hydrology.events.VisibilityChangedEvent;
import star.hydrology.events.VisibilityChangedRaiser;
import star.hydrology.ui.palette.Palette;
import star.hydrology.ui.palette.StepPalette;

@Preferences()
@SignalComponent(raises = { VisibilityChangedRaiser.class, PaletteChangedRaiser.class })
public class Layer extends Layer_generated implements Constants, Listener
{
	private int kind;
	private boolean visible = false;
	private String name = "";
	private Palette palette = new StepPalette("Step");
	private TableModel model = null;
	private boolean enabled = false;

	public boolean isEnabled()
	{
		return enabled;
	}

	public Layer(int kind, TableModel model)
	{
		this.kind = kind;
		this.model = model;
		setVisible(false, false);
	}

	@Override
	public void addNotify()
	{
		super.addNotify();
	}

	void initPreferences()
	{
		setPalette(getPreferences().node(getName()).get("Palette", "Gray/White"), true);
		setVisible(getPreferences().node(getName()).getBoolean("Visible", false), true);
	}

	String getName()
	{
		return name;
	}

	@Handles(raises = {})
	void updateMapLayer(ViewerChangeRaiser r)
	{
		if (r != this && r.getKind() == this.getKind() && r.getViewerName() != null && !r.getViewerName().equals(name))
		{
			name = r.getViewerName();
			enabled = true;
			initPreferences();
			model.fireTableDataChanged();
		}
	}

	@Handles(raises = {})
	void updateVisibility(VisibilityChangedRaiser r)
	{
		if (r != this && r.getKind() == getKind())
		{
			setVisible(r.isMapVisible(), false);
		}
	}

	@Handles(raises = {})
	void updatePalette(PaletteChangedRaiser r)
	{
		if (r != this && r.getKind() == getKind())
		{
			setPalette(r.getPalette(), false);
		}
	}

	Object getValue(int column)
	{
		switch (column)
		{
		case KIND:
			return getKind();
		case VISIBLE:
			return visible;
		case LABEL:
			return name;
		case COLOR:
			return palette;
		default:
			return null;
		}
	}

	void setValue(int column, Object object)
	{
		switch (column)
		{
		case VISIBLE:
			setVisible(((Boolean) object).booleanValue(), true);
			break;
		case LABEL:
			break;
		case COLOR:
			setPalette((Palette) object, true);
			break;
		default:
		}

	}

	boolean isEditable(int column)
	{
		switch (column)
		{
		case VISIBLE:
			return true;
		case LABEL:
			return false;
		case COLOR:
			return true;
		default:
			return false;
		}
	}

	public int getKind()
	{
		return kind;
	}

	public boolean isMapVisible()
	{
		return visible;
	}

	void setVisible(boolean visible, boolean raise)
	{
		this.visible = visible;
		if (raise)
		{
			(new VisibilityChangedEvent(this)).raise();
		}
		model.fireTableDataChanged();
	}

	public Palette getPalette()
	{
		return palette;
	}

	void setPalette(Palette palette, boolean raise)
	{
		this.palette = palette;
		if (raise)
		{
			(new PaletteChangedEvent(this)).raise();
		}
		model.fireTableDataChanged();
	}

	void setPalette(String name, boolean raise)
	{
		ComboBoxModel model = this.model.getColorModel();
		for (int i = 0; i < model.getSize(); i++)
		{
			if (name.equals(((Palette) model.getElementAt(i)).getName()))
			{

				setPalette((Palette) model.getElementAt(i), raise);
				break;
			}
		}
	}

	public void updateWithFilter(String filterName)
	{
		if (filterName != null)
		{
			String defaultVisible = getPreferences().node(filterName).node("default").get("Visible", null);
			String visible = getPreferences().node(filterName).node(getName()).get("Visible", defaultVisible);
			if (visible != null)
			{
				setVisible(Boolean.parseBoolean(visible), true);
			}
		}

	}
}
