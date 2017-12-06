import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

public class CopyOnWriteProxyTest {

	static final String TEXT = "doit";
	static final String OTHER = "Texas";
	static final TargetClass original = new TargetClass(TEXT);
	
	static ITargetClass proxy;

	static class CopyOnWriteProxy implements InvocationHandler {
		
		private Object original;
		private HashMap<String,Object> methods = new HashMap<String,Object>();
		
		public CopyOnWriteProxy(Object original) {
			this.original = original;
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			
			Object result = null;
			
			if (method.getName().startsWith("set") && method.getParameterTypes().length == 1) {
				methods.put(method.getName().replaceFirst("set", "get"),args[0]);
			} else if (methods.containsKey(method.getName())) {
				result = methods.get(method.getName());
			} else {
				result = method.invoke(original,args);
			}
			
			return result;
		}

		@SuppressWarnings("unchecked")
		public static <T> T create(Class<T> interfaceClass, Object original) {
	        return (T)Proxy.newProxyInstance(
        		original.getClass().getClassLoader(),
        		new Class<?>[] { interfaceClass },
        		new CopyOnWriteProxy(original));
		}
	}
	
	@Before
	public void createProxy() {
		proxy = CopyOnWriteProxy.create( ITargetClass.class, original );		
	}
	
	@Test
	public void testNormalInvocation() {
		assertEquals( proxy.getText(), TEXT );
	}
	
	@Test
	public void testSwappingAccessors() {
		proxy.setText(OTHER);	
		assertEquals( proxy.getText(), OTHER );
		assertEquals( original.getText(), TEXT );
	}
	
	@Test
	public void testDependentMethod() {
		proxy.setText(OTHER);	
		assertEquals( proxy.toString(), TEXT );
	}
}
