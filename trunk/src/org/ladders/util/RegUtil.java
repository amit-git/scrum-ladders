package org.ladders.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegUtil {
	public static String match(String html, String expression) {
		Matcher m = Pattern.compile(expression, Pattern.CASE_INSENSITIVE).matcher(html);
		if (m.find()) {
			return m.group(1).trim();
		}
		return null;
	}

	public static ArrayList<String> matchAll(String html, String expression, int groupNum) {

		Matcher m = Pattern.compile(expression, Pattern.CASE_INSENSITIVE).matcher(html);

		ArrayList<String> list = new ArrayList<String>();

		while (m.find()) {
			// Util.log(m.group(groupNum));
			list.add(m.group(groupNum).trim());
		}
		return list;

	}
}
