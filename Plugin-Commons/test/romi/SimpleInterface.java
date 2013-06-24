package test.romi;

import plugin.APIInterface;

public interface SimpleInterface extends APIInterface
{
	String concat(String a, String b);

	String concat(String a, int b);

	void setObject(Object a);

	Object getObject();
}
