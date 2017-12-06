import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

class AbstractProxy implements InvocationHandler {
	
	private Object target;
	
	public AbstractProxy(Object target) {
		this.target = target;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return method.invoke(target,args);
	}

	@SuppressWarnings("unchecked")
	public static <T> T create(Class<T> interfaceClass, AbstractProxy proxy) {
        return (T)Proxy.newProxyInstance(proxy.target.getClass().getClassLoader(),new Class<?>[] {interfaceClass},proxy);			
	}
	
}