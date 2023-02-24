package com.bethefirst.lifeweb.util;

import java.util.regex.Pattern;

public class StringUtil {

	public static String convertPascalToKebab(String s) {
		return Pattern.compile("([A-Z])")
				.matcher(firstLowerCase(s))
				.replaceAll(mr -> "-" + mr.group(0).toLowerCase());
	}

	private static String firstLowerCase(String s) {
		return s.substring(0, 1).toLowerCase() + s.substring(1);
	}

	public static String convertKebabToPascal(String s) {
		return Pattern.compile("-([a-z])")
				.matcher(firstUpperCase(s))
				.replaceAll(mr -> mr.group(1).toUpperCase());
	}

	private static String firstUpperCase(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

}
