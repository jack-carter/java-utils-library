package com.rac.sims.util;

public class Guarantee {

	public static String notNull(String value, String defaultValue) {
		return value == null ? defaultValue : value;
	}
	
	public static String notEmpty(String value, String defaultValue) {
		return value == null || value.length() == 0 ? defaultValue : value;
	}
	
	public static String notBlank(String value, String defaultValue) {
		return isBlank(value) ? defaultValue : value;
	}
	
	public static boolean isBlank(String s) {
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
	
	public static void main(String[] args) {
		
		// checking for NULL values
		assert "NULL".equals( Guarantee.notNull( null, "NULL") );
		assert "NOT NULL".equals( Guarantee.notNull( "NOT NULL", "NULL") );
		
		// checking for empty (e.g. zero-length) values
		assert "EMPTY".equals( Guarantee.notEmpty( "", "EMPTY") );
		assert "NOT EMPTY".equals( Guarantee.notEmpty( "NOT EMPTY", "EMPTY") );
		
		// checking for blank (e.g. non-whitespace characters)
		assert "BLANK".equals( Guarantee.notBlank( " ", "BLANK") );
		assert "NOT BLANK".equals( Guarantee.notBlank( "NOT BLANK", "BLANK") );		
	}

}
