
import java.lang.reflect.*;
import org.junit.*;

import static org.junit.Assert.*;

public class BuilderProxyTest {

	static class BuilderProxy<T> implements InvocationHandler {
		
		private Object target;
		
		@SuppressWarnings("unchecked")
		public static Object with(Class<T> iClass, Object target) {
			return new BuilderProxy(iClass,target);
		}
		
		public BuilderProxy(Class<T> iClass, Object target) {
			this.target = target;
		}

		public T build() {
	        return Proxy.newProxyInstance(
        		target.getClass().getClassLoader(),
        		new Class<?>[] { iClass },
        		new BuilderProxy(target));			
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			return method.invoke(target,args);
		}
		
	}
	
	interface ITarget {
		public String getName();
		public String getTitle();
		public void setName(String name);
		public void setTitle(String title);		
	}
	
	static class Target implements ITarget {
		private String name;
		private String title;
		
		public Target() {
			this.name = "";
			this.title = "";
		}
		
		public Target(String name, String title) {
			this.name = name;
			this.title = title;
		}
		
		public String getName() { return name; }
		public String getTitle() { return title; }
		
		public void setName(String name) { this.name = name; }
		public void setTitle(String title) { this.title = title; }
	}
	
	final static String NAME = "Jack";
	final static String TITLE = "Architect";
	
	@Test
	public void testWith() {
		ITarget target = BuilderProxy.with(new Target())
			.setName(NAME)
			.setTitle(TITLE)
			.as(ITarget.class);
		
		assertNotNull(target);
		assertEquals(target.getName(),NAME);
		assertEquals(target.getTitle(),TITLE);		
	}
}
