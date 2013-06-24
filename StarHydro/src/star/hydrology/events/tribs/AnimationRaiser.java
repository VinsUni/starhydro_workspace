package star.hydrology.events.tribs;

import java.awt.Graphics;

import star.annotations.Raiser;

@Raiser
public interface AnimationRaiser extends star.event.Raiser
{
	public void paint( Graphics g , int width , int height );
}
