package com.rac.sims.util;

import java.util.Optional;

public class ShowOptional {

	public static void main(String args[]) {
		String result = Optional.ofNullable((String)null).orElse("");
		assert "".equals(result);
	}

}
