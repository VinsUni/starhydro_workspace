package app.viewers;

import javax.media.j3d.Texture;

import star.hydrology.data.interfaces.Grid;

class GeometryTile
{
	Grid grid;
	int x0, y0, width, height;
	int scalex, scaley;
	int[] colorIndex;
	float[] coords;
	int[] coords2D;
	float[] textures;
	Texture tx;

	public String toString()
	{
		return coords.length + " " + colorIndex.length;
	}

}