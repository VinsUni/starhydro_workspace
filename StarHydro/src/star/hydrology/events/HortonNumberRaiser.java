package star.hydrology.events;

import java.util.Hashtable;

import star.annotations.Raiser;

@Raiser()
public interface HortonNumberRaiser extends star.event.Raiser
{
	final static String Ra_SLOPE = "Ra-slope";
	final static String Ra_INTERCEPT = "Ra-intercept";
	final static String Ra_UNCERTAINTY = "Ra-uncertainty";

	final static String Rb_SLOPE = "Rb-slope";
	final static String Rb_INTERCEPT = "Rb-intercept";
	final static String Rb_UNCERTAINTY = "Rb-uncertainty";

	final static String Rl_SLOPE = "Rl-slope";
	final static String Rl_INTERCEPT = "Rl-intercept";
	final static String Rl_UNCERTAINTY = "Rl-uncertainty";

	final static String Rs_SLOPE = "Rs-slope";
	final static String Rs_INTERCEPT = "Rs-intercept";
	final static String Rs_UNCERTAINTY = "Rs-uncertainty";

	Hashtable<String, Float> getHortonNumbers();
}
