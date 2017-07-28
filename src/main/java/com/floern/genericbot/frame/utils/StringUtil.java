/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.utils;

public class StringUtil {


	public static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}


	public static boolean isEmpty(String value) {
		return value == null || value.isEmpty();
	}

}
