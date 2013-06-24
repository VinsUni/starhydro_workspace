package test.romi;

public class SimpleClass implements SimpleInterface
{
	private static final long serialVersionUID = 1L;

	Object value;

	public String concat(String a, String b)
	{
		return a + b;
	}

	public String concat(String a, int b)
	{
		return a + b;
	}

	public Object getObject()
	{
		// TODO Auto-generated method stub
		return value;
	}

	public void setObject(Object a)
	{
		value = a;
	}

}
