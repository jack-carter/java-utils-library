package com.rac.common;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value = Parameterized.class)
public class ElapsedTimeFormatterTest {

	@Parameter(value = 0)
	public long millis;
	
	@Parameter(value = 1)
	public String string;
	
	@Parameters
	public static Collection<Object[]> data()  {
		return Arrays.asList(new Object[][] {
			{ 0, "0:00:00.000" },
			{ 1, "0:00:00.001" },
			{ 10, "0:00:00.010" },
			{ 100, "0:00:00.100" },
			{ 1000, "0:00:01.000" },
			{ 10000, "0:00:10.000" },
			{ 60000, "0:01:00.000" },
			{ 600000, "0:10:00.000" },
			{ 3600000, "1:00:00.000" },
			{ 36000000, "10:00:00.000" },
			{ 45296789, "12:34:56.789" }			
		});
	}
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testFormatting() {
		assertEquals( ElapsedTimeFormatter.format(millis), string );
	}

}
