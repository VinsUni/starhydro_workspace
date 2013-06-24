package utils.jnlp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ServiceManagerWrapper
{
	Object svc;
	Method m;
	
	@SuppressWarnings("unchecked")
    public ServiceManagerWrapper()
	{
		try
		{
            @SuppressWarnings("rawtypes")
            Class c = this.getClass().getClassLoader().loadClass("javax.jnlp.ServiceManager" );
			m = c.getMethod("lookup", String.class);						
		}
		catch( Throwable t )
		{
		}
	}
	
	public Object lookup( String name ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NullPointerException
	{
		return m.invoke(svc, name);
	}
}
