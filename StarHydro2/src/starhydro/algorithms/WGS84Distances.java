package starhydro.algorithms;

import starhydro.utils.Point3DDouble;

public class WGS84Distances
{
	double a = 6378137;
	double b = 6356752.31424518;

	public Point3DDouble positionDeg(double fi, double lambda, double h)
	{
		return position(fi * Math.PI / 180, lambda * Math.PI / 180, h);
	}

	public Point3DDouble position(double fi, double lambda, double h)
	{
		double f = (a - b) / a;
		double e = 2 * f - f * f;
		double e_sin = e * Math.sin(fi);
		double N = a / Math.sqrt(1 - e_sin * e_sin);
		double x = (N + h) * Math.cos(fi) * Math.cos(lambda);
		double y = (N + h) * Math.cos(fi) * Math.sin(lambda);
		double z = (N * (1 - e * e) + h) * Math.sin(fi);
		return new Point3DDouble(x, y, z);
	}

	public static void main(String[] args)
	{
		double fi = 3 * 2 * Math.PI / 360 / 60 / 60;
		double lambda = 3 * 2 * Math.PI / 360 / 60 / 60;
		WGS84Distances distance = new WGS84Distances();
		System.out.println(distance.position(fi, lambda, 0));
		System.out.println(distance.position(0, 0, 0));
		System.out.println(distance.position(0, 0, 0).distance(distance.position(fi, lambda, 0)));

	}
}
