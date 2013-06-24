package star.math;

import javax.vecmath.Vector3f;

public class LinearEquations3Variables
{

	public Vector3f solve(Vector3f VA, Vector3f VB, Vector3f VC, Vector3f VD)
	{
		float[] A = new float[3];
		float[] B = new float[3];
		float[] C = new float[3];
		float[] D = new float[3];
		VA.get(A);
		VB.get(B);
		VC.get(C);
		VD.get(D);

		/* use Cramer's rule */
		float aDeter = Determinate(D, B, C);
		float bDeter = Determinate(A, D, C);
		float cDeter = Determinate(A, B, D);
		float dDeter = Determinate(A, B, C);

		if (dDeter == 0)
		{
			if (aDeter == 0 && bDeter == 0 && cDeter == 0)
			{ /* dependent equations */
				/* an infinite number of solutions exist */
				throw new RuntimeException("Dependent");
			}
			else
			{
				throw new RuntimeException("No solution");
			}
		}
		else
		{
			return new Vector3f(aDeter / dDeter, bDeter / dDeter, cDeter / dDeter);
		}
	}

	public float Determinate(float[] a, float[] b, float[] c)
	{
		float det = 0;
		det = a[0] * b[1] * c[2] + b[0] * c[1] * a[2] + c[0] * a[1] * b[2] - a[2] * b[1] * c[0] - b[2] * c[1] * a[0] - c[2] * a[1] * b[0];
		return det;
	}

	void test1()
	{
		Vector3f A = new Vector3f((float) Math.random(), (float) Math.random(), (float) Math.random());
		Vector3f T1 = new Vector3f((float) Math.random(), (float) Math.random(), (float) Math.random());
		Vector3f N1 = new Vector3f();
		Vector3f T2 = new Vector3f((float) Math.random(), (float) Math.random(), (float) Math.random());
		Vector3f N2 = new Vector3f();
		N1.cross(A, T1);
		N2.cross(A, T2);
		System.out.println("Vectors " + A + " " + T1 + " " + N1);
		System.out.println("Vectors " + A + " " + T2 + " " + N2);
		LinearEquations3Variables h = new LinearEquations3Variables();
		Vector3f solution = h.solve(A, T1, N1, N2);
		System.out.println("Solution " + solution);
	}

	void test2()
	{
		Vector3f A = new Vector3f(0, 0, 1);
		Vector3f T1 = new Vector3f(0, 1, 0);
		Vector3f N1 = new Vector3f();
		Vector3f T2 = new Vector3f(1, 1, 0);
		Vector3f N2 = new Vector3f();
		N1.cross(A, T1);
		N2.cross(A, T2);
		System.out.println("Vectors " + A + " " + T1 + " " + N1);
		System.out.println("Vectors " + A + " " + T2 + " " + N2);
		LinearEquations3Variables h = new LinearEquations3Variables();
		Vector3f solution = h.solve(A, T1, N1, N2);
		System.out.println("Solution " + solution);
	}

	void test3()
	{
		Vector3f A = new Vector3f(0, 0, 1);
		Vector3f T1 = new Vector3f(0, 1, 0);
		Vector3f N1 = new Vector3f();
		Vector3f T2 = new Vector3f(1, -1, 0);
		Vector3f N2 = new Vector3f();
		N1.cross(A, T1);
		N2.cross(A, T2);
		System.out.println("Vectors " + A + " " + T1 + " " + N1);
		System.out.println("Vectors " + A + " " + T2 + " " + N2);
		LinearEquations3Variables h = new LinearEquations3Variables();
		Vector3f solution = h.solve(A, T1, N1, N2);
		System.out.println("Solution " + solution);
	}

	public static void main(String[] str)
	{
		LinearEquations3Variables app = new LinearEquations3Variables();
		app.test1();
		app.test2();
		app.test3();

	}

}
