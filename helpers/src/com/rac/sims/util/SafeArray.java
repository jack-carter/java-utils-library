package com.rac.sims.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class SafeArray {

	static <T> List<T> from(T[] array) {
		return array == null
				? Collections.emptyList()
				: Arrays.asList(array);			
	}
	
	public static void main(String args[]) {
		String[] array = null;
		
		try {
			for (String s : array) {
				System.out.println(s);
			}
			
			assert false;
		} catch (NullPointerException ex) {
			System.out.println("for(...) on a 'null' array will NPE");
		}
		
		for (String s : SafeArray.from(array)) {
			assert false;
		}
		System.out.println("for(...) on a SafeArray will not NPE");
		
		SafeArray.from(null).stream().forEach(item -> { assert false; });
		System.out.println("SafeArray.from(...).stream() will be empty");
		
		int i = 0;
		for (String s : SafeArray.from(new String[] { "one", "two", "three" })) {
			i++;
		}
		assert i == 3;
		System.out.println("SafeArray will iterate through a non-null array");
		
		String result = SafeArray.from(new String[] { "one" }).stream().filter(s -> "one".equals(s)).findFirst().orElse(null);
		assert "one".equals(result);
	}
}
