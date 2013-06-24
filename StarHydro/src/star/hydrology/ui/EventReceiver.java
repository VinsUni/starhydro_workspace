package star.hydrology.ui;

import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import mit.swing.xJPanel;
import mit.swing.xJTextArea;
import star.event.Event;
import star.event.Listener;
import star.hydrology.data.interfaces.Projection;
import star.hydrology.events.GridStatisticsProviderChangeEvent;
import star.hydrology.events.InitializeEvent;
import star.hydrology.events.InitializeRaiser;
import star.hydrology.events.LayerChangedEvent;
import star.hydrology.events.LayerChangedRaiser;
import star.hydrology.events.ProjectionChangedEvent;
import star.hydrology.events.ProjectionChangedRaiser;
import star.hydrology.events.RenderableVisibleEvent;
import star.hydrology.events.RenderableVisibleRaiser;
import star.hydrology.events.Select3DEvent;
import star.hydrology.events.UnprojectedMapChangedEvent;
import star.hydrology.events.UnprojectedMapChangedRaiser;
import utils.EventRaiser;

public class EventReceiver extends xJPanel implements Listener, InitializeRaiser
{
	private static final long serialVersionUID = 1L;
	private xJTextArea textArea;

	public EventReceiver()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public void addNotify()
	{
		super.addNotify();
		textArea = new xJTextArea();
		textArea.setColumns(30);
		textArea.setRows(10);
		textArea.isEditable();
		add(new JScrollPane(textArea));
		getAdapter().addHandled(InitializeEvent.class);
		getAdapter().addHandled(LayerChangedEvent.class);
		getAdapter().addHandled(ProjectionChangedEvent.class);
		getAdapter().addHandled(RenderableVisibleEvent.class);
		getAdapter().addHandled(GridStatisticsProviderChangeEvent.class);
		getAdapter().addHandled(UnprojectedMapChangedEvent.class);
		getAdapter().addHandled(Select3DEvent.class);
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				raiseInitEvent();
			}
		});

	}

	void raiseInitEvent()
	{
		EventRaiser.raiseSync(new InitializeEvent(this));
	}

	public void eventRaised(Event event)
	{
		if (event instanceof InitializeEvent)
		{
			append(event.getClass().getName(), event.getTimestamp(), "");
		}
		else if (event instanceof ProjectionChangedEvent)
		{
			Projection p = ((ProjectionChangedRaiser) event.getSource()).getProjection();
			append(event.getClass().getName(), event.getTimestamp(), p.toString());
		}
		else if (event instanceof LayerChangedEvent)
		{
			LayerChangedRaiser p = ((LayerChangedRaiser) event.getSource());
			append(event.getClass().getName(), event.getTimestamp(), "" + p.getLayerKind());
		}
		else if (event instanceof RenderableVisibleEvent)
		{
			RenderableVisibleRaiser p = (RenderableVisibleRaiser) event.getSource();
			append(event.getClass().getName(), event.getTimestamp(), p.isMapVisible() + " " + p.getClass().getName());
		}
		else if (event instanceof UnprojectedMapChangedEvent)
		{
			UnprojectedMapChangedRaiser p = (UnprojectedMapChangedRaiser) event.getSource();
			append(event.getClass().getName(), event.getTimestamp(), "" + p.getMap());
		}
		else
		{
			append(event.toString(), event.getTimestamp(), "");
		}
	}

	private void append(String event, long timestamp, String aux)
	{
		set.add(new Data(event, timestamp, aux));
		StringBuffer sb = new StringBuffer();
		Iterator i = set.iterator();
		while (i.hasNext())
		{
			sb.append(i.next());
			sb.append("\n");
		}
		textArea.setText(sb.toString());
	}

	private TreeSet<Data> set = new TreeSet<Data>();

	private static class Data implements Comparable
	{
		private String event;
		private long timestamp;
		private String aux;

		Data(String event, long timestamp, String aux)
		{
			this.event = event;
			this.timestamp = timestamp;
			this.aux = aux;
		}

		public int compareTo(Object arg0)
		{
			if (arg0 instanceof Data)
			{
				Data a = this;
				Data b = (Data) arg0;
				if (a.timestamp < b.timestamp)
				{
					return -1;
				}
				else if (a.timestamp > b.timestamp)
				{
					return 1;
				}
				else
				{
					return a.event.compareTo(b.event);
				}
			}
			else
			{
				return -1;
			}
		}

		public String toString()
		{
			return timestamp + " " + event + " " + aux;
		}
	}
}
