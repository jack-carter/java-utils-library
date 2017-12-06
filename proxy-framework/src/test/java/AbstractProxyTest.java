import static org.junit.Assert.*;

import org.junit.Test;

public class AbstractProxyTest {

	static final String TEXT = "doit";
	
	static class Subclass extends AbstractProxy {
		
		public static ITargetClass create(ITargetClass target) {
			return AbstractProxy.create(ITargetClass.class, new Subclass(target));
		}
		
		public Subclass(Object target) {
			super(target);
		}

	}
	
	@Test
	public void testInvocation() {
		ITargetClass proxy = Subclass.create(new TargetClass(TEXT));
		assertEquals( proxy.getText(), TEXT );
	}

}
