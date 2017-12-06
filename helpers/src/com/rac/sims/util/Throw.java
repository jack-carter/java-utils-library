package com.rac.sims.util;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.management.BadStringOperationException;

/**
 * Throw is a convenience class that allows a declarative style of argument
 * checking by allowing developers to use statements such as:
 * 
 * {@code
 * Throw.ifNull(var);
 * Throw.ifNotNull(var);
 * Throw.ifEmpty(var);
 * Throw.ifNotEmpty(var);
 * Throw.ifBlank(var);
 * Throw.ifNotBlank(var);
 * }
 *
 * There are variants for each of the above methods that allow a developer to
 * provide a custom message that will be inserted into any exception that is 
 * throw, as in:
 * 
 * {@code
 * Throw.ifNull(var,"var is NULL);
 * Throw.ifNotNull(var,"var is NOT NULL");
 * Throw.ifEmpty(var,"var is EMPTY");
 * Throw.ifNotEmpty(var,"var is NOT EMPTY");
 * Throw.ifBlank(var,"var is BLANK");
 * Throw.ifNotBlank(var,"var is NOT BLANK");
 * }
 * 
 * Or, developers can specify a custom message like this:
 * 
 * {@code
 * Throw.withMessage("we have a NULL argument").ifNull(var);
 * }
 * 
 * Providing a message in this fashion allows for a compact statement to check
 * multiple variables at once, and use the same message if an exception is thrown:
 * 
 * {@code
 * Throw.withMessage("we have a NULL argument")
 * 	    .ifNull(var1)
 *      .ifNull(var2)
 *      .ifNull(var3);
 * }
 * 
 * Additionally, custom messages provided using the {@code withMessage()} method
 * are allowed to provide format specifiers in the message, following the pattern
 * of {@see String.format()}. When doing this developers may now pass along a set
 * of arguments for the message, rather than a simple text string as in:
 * 
 * {@code
 * Throw.withMessage("%s is NULL (need to call %s)")
 *      .ifNull(var1,"var1","Jim")
 *      .ifNull(var2,"var2","Audrey")
 *      .ifNull(var3,"var3","Mickey");
 * }
 * 
 * Which would result in the following style of exception:
 * 
 * {@code
 * Exception in thread "main" java.lang.NullPointerException: var1 is NULL (need to call Jim)
 * } 
 * 
 * Normally use of these methods will throw either a {@see NullPointerException}
 * or {@see IllegalArgumentException}, depending upon the method invoked, and the
 * condition of the variable being checked.
 * 
 * Developers are allowed, however, to specify the use of a custom exception in
 * place of these two standard exceptions, by prefixing the checks with:
 * 
 * {@code}
 * Throw.exception( MyCustomException.class )
 *      .ifNull(var);
 *      
 * Throw.exception( MyCustomerException.class )
 *      .withMessage("%s is NULL (need to call %s)")
 *      .ifNull(var1,"var1","Jim")
 *      .ifNull(var2,"var2","Audrey")
 *      .ifNull(var3,"var3","Mickey");
 * {@code}
 *
 * @author Jack Carter (jack.carter@shinobigroup.com)
 */
public class Throw {

	// Prefixes
	
	public static CustomThrower exception(Class<? extends Exception> exception) {
		return new CustomThrower(exception);
	}
	
	public static MessageDecorator withMessage(String msg) {
		return new Thrower().withMessage(msg);
	}
	
	// Without any message
	
	public static Thrower ifNull(Object o)     { return ifNull(o,null); }
	public static Thrower ifEmpty(String s)    { return ifEmpty(s,null); }
	public static Thrower ifBlank(String s)    { return ifBlank(s,null); }
	
	public static Thrower ifNotNull(Object o)  { return ifNotNull(o,null); }
	public static Thrower ifNotEmpty(String s) { return ifNotEmpty(s,null); }
	public static Thrower ifNotBlank(String s) { return ifNotBlank(s,null); }
	
	// Including a message
	
	public static Thrower ifNull(Object o,String msg)     { return new Thrower().ifNull(o,msg); }	
	public static Thrower ifEmpty(String s,String msg)    { return new Thrower().ifEmpty(s,msg); }	
	public static Thrower ifBlank(String s,String msg)    { return new Thrower().ifBlank(s,msg); }
	
	public static Thrower ifNotNull(Object o,String msg)  { return new Thrower().ifNotNull(o,msg); }
	public static Thrower ifNotEmpty(String s,String msg) { return new Thrower().ifNotEmpty(s,msg); }	
	public static Thrower ifNotBlank(String s,String msg) { return new Thrower().ifNotBlank(s,msg); }
	
	// Implementation Support
	
	/**
	 * Thrower does just that, when exceptions are encountered, it simply
	 * throws the standard exceptions of {@see NullPointerException} and
	 * {@see IllegalArgumentException}.
	 */
	static public class Thrower {
				
		public Thrower() {}
		
		// Prefixes
		
		public MessageDecorator withMessage(String msg) {
			return new MessageDecorator(this,msg);
		}
		
		// NULL checks
		
		public Thrower ifNull(Object o) {
			ifNull(o,null);
			return this;
		}

		public Thrower ifNotNull(Object o) {
			ifNotNull(o,null);
			return this;
		}
		
		public Thrower ifNull(Object o,String msg) {
		    if (o == null) nullPointer(msg);
			return this;
		}
		
		public Thrower ifNotNull(Object o,String msg) {
		    if (o != null) illegalArgument(msg);
			return this;
		}
		
		// EMTPY checks
		
		public Thrower ifEmpty(String s) {
			ifEmpty(s,null);
			return this;
		}
		
		public Thrower ifNotEmpty(String s) {
			ifNotEmpty(s,null);
			return this;
		}
	
		public Thrower ifEmpty(String s,String msg) {
			if (s == null) nullPointer(msg);
			if (s.length() == 0) illegalArgument(msg);
			return this;
		}
		
		public Thrower ifNotEmpty(String s,String msg) {
			if (s != null && s.length() != 0) illegalArgument(msg);
			return this;
		}
		
		// BLANK checks
		
		public Thrower ifBlank(String s) {
			ifBlank(s,null);
			return this;
		}
		
		public Thrower ifNotBlank(String s) {
			ifNotBlank(s,null);
			return this;
		}
	
		public Thrower ifBlank(String s,String msg) {
			if (s == null) nullPointer(msg);
			if (isBlank(s)) illegalArgument(msg);
			return this;
		}
		
		public Thrower ifNotBlank(String s,String msg) {
			if (isBlank(s) == false) illegalArgument(msg);
			return this;
		}
		
		// Implementation support
		
		protected boolean isBlank(String s) {
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
		
		protected void nullPointer(String msg) {
			throw new NullPointerException(msg);
		}
		
		protected void illegalArgument(String msg) {
			throw new IllegalArgumentException(msg);
		}
		
	}
	
	/**
	 * CustomThrower allows for specifying the use of a custom exception
	 * by use of a prefix to the statement. This class takes charge of 
	 * creating an instance of that custom exception, and then throwing it.
	 * 
	 * If it encounters any problems along the way, it simply uses the
	 * standard exceptions, in the same manner as the {@see Thrower} class.
	 */
	static public class CustomThrower extends Thrower {
		
		private Class<? extends Exception> exception;
		
		public CustomThrower() {}
		
		public CustomThrower(Class<? extends Exception> exception) {
			this.exception = exception;
		}
		
		public CustomThrower ifTrue(boolean expr) {
		    ifTrue(expr,null);
			return this;
		}
		
		public CustomThrower ifFalse(boolean expr) {
			ifFalse(expr,null);
			return this;
		}
		
		public CustomThrower ifTrue(boolean expr,String msg) {
		    if (expr) throwException(msg, (m)-> { throw new AssertionError(m); } );
			return this;
		}
		
		public CustomThrower ifFalse(boolean expr,String msg) {
		    if (!expr) throwException(msg, (m)-> { throw new AssertionError(m); } );
			return this;
		}
		
		protected void nullPointer(String msg) {
		    throwException(msg, (m)-> { throw new NullPointerException(m); } );
		}
			
		protected void illegalArgument(String msg) {
		    throwException(msg, (m)-> { throw new IllegalArgumentException(m); } );
		}
		
		/**
		 * DefaultThrowFunction provides a simply call specification to allow lambdas.
		 */
		private interface DefaultThrowFunction {
			public void run(String msg);
		}
		
		/**
		 * throwException(String,DefaultThrowFunction) attempts to create an instance of the
		 * custom exception specific by {@see Throw.exception}, and if successful throws the
		 * exception created. If unsuccesful it calls the {@see defaultBehavior} function as
		 * the default action.
		 * 
		 * @param msg the formatted and completed String that will be included in the exception.
		 * @param defaultBehavior a lambda function that throws a suitable standard exception.
		 */
		private void throwException(String msg,DefaultThrowFunction defaultBehavior) {
			// short-circuit our behavior if no custom exception is specified
			if (this.exception == null)
				defaultBehavior.run(msg);
			
			// OK then, try and create an instance of the custom exception
			else try {
		    	throw new RuntimeException(this.exception.getConstructor(String.class).newInstance(msg));
		    } catch (InstantiationException e) {
		    	defaultBehavior.run(msg);
		    } catch (IllegalAccessException e) {
		    	defaultBehavior.run(msg);
		    } catch (IllegalArgumentException e) {
		    	defaultBehavior.run(msg);
		    } catch (InvocationTargetException e) {
		    	defaultBehavior.run(msg);
		    } catch (NoSuchMethodException e) {
		    	defaultBehavior.run(msg);
		    } catch (SecurityException e) {
		    	defaultBehavior.run(msg);
		    }
		}

	}
	
	/**
	 * MessageDecorator allows the addition of a custom message, one that uses a
	 * series of format specifiers in the same fashion as {@String.format()} to
	 * build custom messages.
	 * 
	 * By allowing this, it also provide additional methods so developers can pass
	 * variable parameters as part of the message as well.
	 */
	static public class MessageDecorator {
		
		private String msg;
		private Thrower thrower;
		
		public MessageDecorator(Thrower thrower, String msg) {
			this.msg = msg;
			this.thrower = thrower;
		}
		
		// Instance Methods
		
		public MessageDecorator ifNull(Object o, Object... arguments) {
			thrower.ifNull(o,format(arguments));
			return this;
		}

		public MessageDecorator ifEmpty(String s, Object... arguments) {
			thrower.ifEmpty(s,format(arguments));
			return this;
		}

		public MessageDecorator ifBlank(String s, Object... arguments) {
			thrower.ifBlank(s,format(arguments));
			return this;
		}

		public MessageDecorator ifNotNull(Object o, Object... arguments) {
			thrower.ifNotNull(o,format(arguments));
			return this;
		}
		
		public MessageDecorator ifNotEmpty(String s, Object... arguments) {
			thrower.ifNotEmpty(s,format(arguments));
			return this;
		}

		public MessageDecorator ifNotBlank(String s, Object... arguments) {
			thrower.ifNotBlank(s,format(arguments));
			return this;
		}

		// Implementation Support
		
		private String format(Object...arguments) {
			String message = "";
			try { message = String.format(this.msg,arguments); } catch (Throwable t) {}
			return message;
		}

	}
	
	public static void main(String[] args) {
		
		/* We specifically DO NOT allow these constructs
		Throw.ifNull(null).exception( BadStringOperationException.class );
		Throw.ifNull(null).withMessage("");
		Throw.withMessage("").exception( BadStringOperationException.class );
		Throw.withMessage("").ifNull(null).exception( BadStringOperationException.class );
		 */

		/* Basic tests */
		
		{
			boolean assume = false;
			try { 
				Throw.ifNull(null); 
			} catch(NullPointerException ex) {
				assume = true;
			} finally { 
				assert assume; 
			}
		}
			
		{
			boolean assume = true;
			try { 
				Throw.ifNull(""); 
			} catch (Throwable t) {
				assume = false;
			} finally { 
				assert assume; 
			}
		}
			
		{
			boolean assume = false;
			try {
				Throw.ifEmpty("");
			} catch (IllegalArgumentException ex) {
				assume = true;
			} finally {
				assert assume;
			}
		}
		
		{
			boolean assume = false;
			try {
				Throw.ifBlank(" ");		
			} catch (IllegalArgumentException ex) {
				assume = true;
			} finally {
				assert assume;
			}
		}
		
		{
			boolean assume = false;
			try {
				Throw.ifNotNull("");
			} catch (IllegalArgumentException ex) {
				assume = true;
			} finally {
				assert !assume;
			}
		}
		
		{
			boolean assume = false;
			try {
				Throw.ifNotEmpty(" ");
			} catch (IllegalArgumentException ex) {
				assume = true;
			} finally {
				assert assume;
			}
		}
		
		{
			boolean assume = false;
			try {
				Throw.ifNotBlank("123");
			} catch (IllegalArgumentException ex) {
				assume = true;
			} finally {
				assert assume;
			}
		}
		
		/* Basic tests (with messages) */
		
		{
			boolean assume = false;
			try { 
				Throw.ifNull(null,"NULL"); 
			} catch(NullPointerException ex) {
				if (ex.getMessage().equals("NULL")) assume = true;
			} catch(IllegalArgumentException ex) {
				assume = false;
			} catch (Throwable t) {
				assume = false;
			} finally { 
				assert assume; 
			}
		}
			
		{
			boolean assume = true;
			try { 
				Throw.ifNull("","NOT NULL"); 
			} catch(NullPointerException ex) {
				assume = false;
			} catch(IllegalArgumentException ex) {
				if (ex.getMessage().equals("NOT NULL")) assume = false;
			} catch (Throwable t) {
				assume = false;
			} finally { 
				assert assume; 
			}
		}
			
		{
			boolean assume = false;
			try {
				Throw.ifEmpty("", "EMPTY");
			} catch(NullPointerException ex) {
				assume = false;
			} catch (IllegalArgumentException ex) {
				if (ex.getMessage().equals("EMPTY")) assume = true;
			} catch (Throwable t) {
				assume = false;
			} finally {
				assert assume;
			}
		}
		
		{
			boolean assume = false;
			try {
				Throw.ifBlank(" ","BLANK");
			} catch(NullPointerException ex) {
				assume = false;
			} catch (IllegalArgumentException ex) {
				if (ex.getMessage().equals("BLANK")) assume = true;
			} catch (Throwable t) {
				assume = false;
			} finally {
				assert assume;
			}
		}
		
		{
			boolean assume = false;
			try {
				Throw.ifNotNull("","NOT NULL");
			} catch(NullPointerException ex) {
				assume = false;
			} catch (IllegalArgumentException ex) {
				if (ex.getMessage().equals("NOT NULL")) assume = true;
			} catch (Throwable t) {
				assume = false;
			} finally {
				assert assume;
			}
		}
		
		{
			boolean assume = false;
			try {
				Throw.ifNotEmpty(" ","NOT EMPTY");
			} catch(NullPointerException ex) {
				assume = false;
			} catch (IllegalArgumentException ex) {
				if (ex.getMessage().equals("NOT EMPTY")) assume = true;
			} catch (Throwable t) {
				assume = false;
			} finally {
				assert assume;
			}
		}
		
		{
			boolean assume = false;
			try {
				Throw.ifNotBlank("123","NOT BLANK");
			} catch(NullPointerException ex) {
				assume = false;
			} catch (IllegalArgumentException ex) {
				if (ex.getMessage().equals("NOT BLANK")) assume = true;
			} catch (Throwable t) {
				assume = false;
			} finally {
				assert assume;
			}
		}
		
		/* Test that chaining works */

		{
			boolean assume = true;
			try {
				Throw
				.ifNull("")
				.ifEmpty(" ")
				.ifBlank("123")
				.ifNotNull(null)
				.ifNotEmpty("")
				.ifNotBlank(" ");				
			} catch (NullPointerException ex) {
				assume = false;
			} catch (IllegalArgumentException ex) {
				assume = false;
			} catch (Throwable t) {
				assume = false;
			} finally {
				assert assume;
			}
		}
		
		/* Test that shared messaging works */
		
		{
			boolean assume = false;
			try {
				Throw.withMessage("FAILED")
				.ifNull(null);				
			} catch (NullPointerException ex) {
				if (ex.getMessage().equals("FAILED")) assume = true;
			} catch (IllegalArgumentException ex) {
				assume = false;
			} catch (Throwable t) {
				assume = false;
			} finally {
				assert assume;
			}
		}
		
		Throw.withMessage("FAILED")
			.ifEmpty("")
			.ifBlank(" ")
			.ifNotNull("")
			.ifNotEmpty(" ")
			.ifNotBlank("123");
		
		/* Test that formatted messaging works */
		
		Throw.withMessage("FAILED on %s")
			.ifNull(null, "NULL")
			.ifEmpty("", "EMPTY")
			.ifBlank(" ", "BLANK")
			.ifNotNull("", "NOT NULL")
			.ifNotEmpty(" ", "NOT EMPTY")
			.ifNotBlank("123", "NOT BLANK");
		
		/* Test that custom exceptions work */
		
		Throw.exception( BadStringOperationException.class )
			.ifNull("")
			.ifEmpty(" ")
			.ifBlank("123")
			.ifNotNull(null)
			.ifNotEmpty("")
			.ifNotBlank(" ");

		Throw.exception( BadStringOperationException.class ).withMessage("FAILED on %s")
			.ifNull(null, "NULL")
			.ifEmpty("", "EMPTY")
			.ifBlank(" ", "BLANK")
			.ifNotNull("", "NOT NULL")
			.ifNotEmpty(" ", "NOT EMPTY")
			.ifNotBlank("123", "NOT BLANK");
		
		/* Test that extensions to custom exceptions work */

		Throw.exception( BadStringOperationException.class )
			.ifTrue(true);
		
		Throw.exception( BadStringOperationException.class )
			.ifFalse(false);

	}
	
}
