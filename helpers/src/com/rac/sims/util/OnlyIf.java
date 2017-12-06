package com.rac.sims.util;

public class OnlyIf {

	public interface OnlyIfFunction {
		public void apply(String value);
	}
	
	public static void notNull(String value,OnlyIfFunction function) {
		if (function != null && value != null)
			function.apply(value);
	}
	
	public static void isNull(String value,OnlyIfFunction function) {
		if (function != null && value == null)
			function.apply(value);
	}
	
	public static void notEmpty(String value,OnlyIfFunction function) {
		if (function != null && (value != null && value.length() != 0))
			function.apply(value);
	}
	
	public static void isEmpty(String value,OnlyIfFunction function) {
		if (function != null && (value == null || value.length() == 0))
			function.apply(value);
	}
	
	public static void notBlank(String value,OnlyIfFunction function) {
		if (function != null && Guarantee.isBlank(value) == false)
			function.apply(value);
	}
	
	public static void isBlank(String value,OnlyIfFunction function) {
		if (function != null && Guarantee.isBlank(value))
			function.apply(value);
	}
	
	public static void main(String[] args) {

		// checking for NULL values
		OnlyIf.notNull( null, s -> { assert false; } );
		OnlyIf.isNull ( null, s -> { assert s == null; } );
		
		// checking for empty (e.g. zero-length) values
		OnlyIf.notEmpty( "", s -> { assert false; } );
		OnlyIf.isEmpty( "", s -> { assert "".equals(s); } );		

		// checking for blank (e.g. non-whitespace) values
		OnlyIf.notBlank( " ", s -> { assert false; } );
		OnlyIf.isBlank( " ", s -> { assert " ".equals(s); } );	}

}
