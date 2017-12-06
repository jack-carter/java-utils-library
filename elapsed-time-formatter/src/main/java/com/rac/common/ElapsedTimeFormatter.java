package com.rac.common;

/**
 * ElapsedTimeFormatter is a simple implementation that converts a number
 * of milliseconds into the human-readable format of 0:00:00.000 (H:MM:SS.mmm).
 * 
 * @author Jack Carter
 * @version 1.0.0
 * @since 11 September 2017 
 */
public class ElapsedTimeFormatter {

	// millisecond values for constituent time epochs
	static public long PER_SECOND = 1000;
	static public long PER_MINUTE = PER_SECOND * 60;
	static public long PER_HOUR   = PER_MINUTE * 60;
	static public long PER_DAY    = PER_HOUR   * 24;
	
	/**
	 * @param millis an elapsed time interval in milliseconds
	 * @return a human-readable elapsed time interval in the form H:MM:SS.mmm
	 */
	static public String format(long millis) {
		return new ElapsedTime(millis).toString();
	}
	
	/**
	 * ElapsedTime is provided as a private implementation class, so that
	 * if ever necessary it can be extracted for other uses. The implementation
	 * of this class simply splits an elapsed time interval (in milliseconds)
	 * into its constituent hours, minutes, seconds, and milliseconds.
	 */
	static private class ElapsedTime {
		
		private long hours, minutes, seconds, millis;
		
		public ElapsedTime(long elapsed) {
			
			long remainder = elapsed;
			
			this.hours   = remainder / PER_HOUR;
			remainder    = remainder % PER_HOUR;
			
			this.minutes = remainder / PER_MINUTE;
			remainder    = remainder % PER_MINUTE;
			
			this.seconds = remainder / PER_SECOND;
			this.millis  = remainder % PER_SECOND;
			
		}
		
		// Accessors
		public long hours()        { return hours;   }
		public long minutes()      { return minutes; }	
		public long seconds()      { return seconds; }	
		public long milliseconds() { return millis;  }
		
		public String toString() {
			return String.format("%d:%02d:%02d.%03d", hours(), minutes(), seconds(), milliseconds());
		}
	}
}
