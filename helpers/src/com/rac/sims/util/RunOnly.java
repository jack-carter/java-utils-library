package com.rac.sims.util;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * RunOnly is a convenience class that allows multiple predicate checks
 * to be applied to the conditional execution of a specified code block.
 * 
 * Example:
 * {@code
 * RunOnly.when("123")
 *     .isNotNull()
 *     .isNotEmpty()
 *     .isNotBlank()
 *     .isEqualTo("123")
 *     .isNotEqualTo("456")
 *     .isTrue("123"::equals)
 *     .isTrue(s -> "123".equals(s) )
 *     .isTrue(s -> s.length() == 3 )
 *     .isFalse("456"::equals)
 *     .isFalse(s -> "456".equals(s) )
 *     .isFalse(s -> s.length() == 4 )
 *     .then(s -> { System.out.println(s); });
 * }
 * 
 * The code block passed to the {@see RunOnly.then()} method will only be
 * invoked if ALL specified conditions are true. If any condition results
 * in a false, the code block is not invoked.
 *  
 * @author Jack Carter (jack.carter@shinobigroup.com)
 */
public class RunOnly {

	public static ActualVerifier when(String s) {
		return new ActualVerifier(s);
	}
	
	/**
	 * This interface calls out every check that can be performed by the 
	 * RunOnly class. It serves as a base for swizzling to a different
	 * implementation should we fail any predicate along the way.
	 */
	static interface Verifier {
		public Verifier isNull();
		public Verifier isNotNull();
		public Verifier isEmpty();
		public Verifier isNotEmpty();
		public Verifier isBlank();
		public Verifier isNotBlank();
		public Verifier isEqualTo(String s);
		public Verifier isNotEqualTo(String s);
		public Verifier isTrue(Predicate<String> f);
		public Verifier isFalse(Predicate<String> f);
		public void then(Consumer<String> f);
	}
	
	/**
	 * This implementation class serves as a safeguard in that it will 
	 * perform no operations against the target, and will behave as if
	 * the target failed one of its predicates.
	 */
	static class EmptyVerifier implements Verifier {
		public Verifier isNull()						{ return this; }
		public Verifier isNotNull() 					{ return this; }
		public Verifier isEmpty()						{ return this; }
		public Verifier isNotEmpty() 					{ return this; }
		public Verifier isBlank()						{ return this; }
		public Verifier isNotBlank() 					{ return this; }
		public Verifier isEqualTo(String s) 			{ return this; }
		public Verifier isNotEqualTo(String s) 		  	{ return this; }
		public Verifier isTrue(Predicate<String> f) 	{ return this; }
		public Verifier isFalse(Predicate<String> f) 	{ return this; }
		public void then(Consumer<String> f) 			{}
	}
	
	/**
	 * As its name implies, this implementation does the actual work of
	 * confirming all the predicates declared for a target.
	 * 
	 * Should a target succeed in passing all of its predicates, this
	 * implementation will continue by invoking the {@see Consumer} passed
	 * to the {@see then(Consumer)} method.
	 * 
	 * If at any time
	 * a target fails a predicate, this implementation will swizzle to an
	 * instance of the {@see EmptyVerifier} to ensure no other operations
	 * cause conflicts or runtime errors with the target.
	 */
	static class ActualVerifier implements Verifier {
		
		private String target;
		
		public ActualVerifier(String s) {
			this.target = s;
		}
		
		public Verifier isNull() {
			return ifTrue(target == null);
		}
		
		public Verifier isNotNull() {
			return ifFalse(target == null);
		}
		
		public Verifier isEmpty() {
			return ifTrue(target == null || target.length() == 0);
		}
		
		public Verifier isNotEmpty() {
			return ifFalse(target == null || target.length() == 0);
		}
		
		public Verifier isBlank() {
	        return ifTrue(isBlank(this.target));
		}
		
		public Verifier isNotBlank() {
	        return ifFalse(isBlank(this.target));
		}
		
		public Verifier isEqualTo(String s) {
			return ifTrue(this.target == s || (s != null && s.equals(this.target)));
		}
		
		public Verifier isNotEqualTo(String s) {
			return ifTrue(this.target != s || (s != null && !s.equals(this.target)));
		}
		
		public Verifier isTrue(Predicate<String> f) {
			return ifTrue(f.test(this.target));
		}
		
		public Verifier isFalse(Predicate<String> f) {
			return ifFalse(f.test(this.target));
		}
		
		public void then(Consumer<String> f) {
			f.accept(this.target);
		}
		
		// Implementation Support
		
		private boolean isBlank(String s) {
	        int strLen;
	        if (s == null || (strLen = s.length()) == 0) {
	            return true;
	        }
	        for (int i = 0; i < strLen; i++) {
	            if (Character.isWhitespace(s.charAt(i)) == false) {
	            	return false;
	            }
	        }
			return true;
		}
		
		private Verifier ifTrue(boolean expr) {
			return swizzleIf(expr,false);
		}
		
		private Verifier ifFalse(boolean expr) {
			return swizzleIf(expr,true);
		}
		
		private Verifier swizzleIf(boolean expr,boolean wrong) {
			return expr == wrong ? new EmptyVerifier() : this;
		}

	}

	// Example use
	
	public static void main(String[] args) {
		
		{
			boolean assume = true;
			try {
			RunOnly.when("123")
		      .isNotNull()
		      .isNotEmpty()
		      .isNotBlank()
		      .isEqualTo("123")
		      .isNotEqualTo("456")
		      .isTrue("123"::equals)
		      .isTrue(s -> "123".equals(s) )
		      .isTrue(s -> s.length() == 3 )
		      .isFalse("456"::equals)
		      .isFalse(s -> "456".equals(s) )
		      .isFalse(s -> s.length() == 4 )
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}
		}

		{
			boolean assume = false;
			try {
			RunOnly.when(null)
		      .isNotNull()
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}

		{
			boolean assume = true;
			try {
			RunOnly.when("")
		      .isNotNull()
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
		
		{
			boolean assume = false;
			try {
			RunOnly.when(null)
		      .isNotEmpty()
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
		
		{
			boolean assume = false;
			try {
			RunOnly.when("")
		      .isNotEmpty()
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
				
		{
			boolean assume = true;
			try {
			RunOnly.when(" ")
		      .isNotEmpty()
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
		
		{
			boolean assume = false;
			try {
			RunOnly.when("")
		      .isNotBlank()
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
		
		{
			boolean assume = false;
			try {
			RunOnly.when("")
		      .isNotBlank()
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
		
		{
			boolean assume = false;
			try {
			RunOnly.when(" ")
		      .isNotBlank()
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
		
		{
			boolean assume = true;
			try {
			RunOnly.when("123")
		      .isNotBlank()
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
		
		{
			boolean assume = true;
			try {
			RunOnly.when(null)
		      .isEqualTo(null)
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
		
		{
			boolean assume = true;
			try {
			RunOnly.when("")
		      .isEqualTo("")
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
		
		{
			boolean assume = true;
			try {
			RunOnly.when("123")
		      .isEqualTo("123")
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
		
		{
			boolean assume = false;
			try {
			RunOnly.when(null)
		      .isEqualTo("123")
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
		
		{
			boolean assume = false;
			try {
			RunOnly.when("")
		      .isEqualTo("123")
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
		
		{
			boolean assume = false;
			try {
			RunOnly.when(" ")
		      .isEqualTo("123")
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
		
		{
			boolean assume = false;
			try {
			RunOnly.when("456")
		      .isEqualTo("123")
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
		
		{
			boolean assume = true;
			try {
			RunOnly.when(null)
		      .isNotEqualTo("456")
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
		
		{
			boolean assume = true;
			try {
			RunOnly.when("")
		      .isNotEqualTo("456")
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
		
		{
			boolean assume = true;
			try {
			RunOnly.when("123")
		      .isNotEqualTo("456")
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
		
		{
			boolean assume = false;
			try {
			RunOnly.when("456")
		      .isNotEqualTo("456")
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
		
		{
			boolean assume = false;
			try {
			RunOnly.when(null)
		      .isTrue("123"::equals)
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
		
		{
			boolean assume = false;
			try {
			RunOnly.when("")
		      .isTrue("123"::equals)
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
		
		{
			boolean assume = true;
			try {
			RunOnly.when("123")
		      .isTrue("123"::equals)
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
		
		{
			boolean assume = false;
			try {
			RunOnly.when("456")
		      .isTrue("123"::equals)
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
		
		{
			boolean assume = false;
			try {
			RunOnly.when(null)
		      .isTrue(s -> s != null)
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
		
		{
			boolean assume = true;
			try {
			RunOnly.when(null)
		      .isTrue(s -> s == null)
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
		
		{
			boolean assume = false;
			try {
			RunOnly.when(null)
		      .isFalse(s -> s == null)
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
		
		{
			boolean assume = true;
			try {
			RunOnly.when(null)
		      .isFalse(s -> s != null)
		      .then(s -> { assert false; });
			} catch (AssertionError ex) {
			  assume = !assume;
			} finally {
			  assert !assume;
			}			
		}
		
	}

}
