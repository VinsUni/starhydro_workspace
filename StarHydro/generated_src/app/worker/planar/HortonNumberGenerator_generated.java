/* Generated by star.annotations.GeneratedClass, all changes will be lost */

package app.worker.planar;

public abstract class HortonNumberGenerator_generated extends java.lang.Object implements star.event.EventController, star.event.Listener, star.hydrology.events.HortonNumberRaiser
{
	private star.event.Adapter adapter;
	private java.util.prefs.Preferences preferences = null;
	private static final long serialVersionUID = 1L;

	public  HortonNumberGenerator_generated()
	{
		super();
	}
	 
	public void addNotify()
	{
		getAdapter().addHandled(star.hydrology.events.StreamOrderStatisticsEvent.class );
	}
	 
	public void eventRaised(final star.event.Event event)
	{
		eventRaisedEventHandle(event);
	}
	 
	private void eventRaisedEventHandle(star.event.Event event)
	{
		if( event.getClass().getName().equals( "star.hydrology.events.StreamOrderStatisticsEvent" ) ) { 
			star.hydrology.events.StreamOrderStatisticsRaiser r = (star.hydrology.events.StreamOrderStatisticsRaiser)event.getSource();
			if(event.isValid())
			{
				handleEvent( r );
			}
			else
			{
				handleInvalidEvent( r );
			}
		}
	}
	 
	public star.event.Adapter getAdapter()
	{
		if( adapter == null )
		{
			adapter = new star.event.Adapter(this);
		}
		return adapter;
	}
	 
	public java.util.prefs.Preferences getPreferences(java.lang.String name)
	{
		try
		{
			plugin.preferences.Preferences pref = (plugin.preferences.Preferences) plugin.Loader.getDefaultLoader().getPlugin(plugin.preferences.Preferences.class.getName(), plugin.preferences.PreferencesImplementation.class.getName());
			this.preferences = pref.getPreferences(name);
		}
		catch( plugin.PluginException ex )
		{
			ex.printStackTrace();
		}
		return preferences;
	}
	 
	public java.util.prefs.Preferences getPreferences()
	{
		if( preferences == null )
		{
			try
			{
				plugin.preferences.Preferences pref = (plugin.preferences.Preferences) plugin.Loader.getDefaultLoader().getPlugin(plugin.preferences.Preferences.class.getName(), plugin.preferences.PreferencesImplementation.class.getName());
				this.preferences = pref.getPreferences("app.worker.planar.HortonNumberGenerator");
			}
			catch( plugin.PluginException ex )
			{
				ex.printStackTrace();
			}
		}
		return preferences;
	}
	 
	abstract void handleEvent(star.hydrology.events.StreamOrderStatisticsRaiser raiser);
	 
	void handleInvalidEvent(star.hydrology.events.StreamOrderStatisticsRaiser raiser)
	{
	}
	 
	public void raise_HortonNumberEvent()
	{
		(new star.hydrology.events.HortonNumberEvent(this)).raise();
	}
	 
	public void removeNotify()
	{
		getAdapter().removeHandled(star.hydrology.events.StreamOrderStatisticsEvent.class );
	}
	 
}