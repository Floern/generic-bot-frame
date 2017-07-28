/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.utils;

import java.util.Arrays;

public class ChatPrinter {


	public static String formatException(Throwable e) {
		StringBuilder sb = new StringBuilder(512);
		String linePrefix = "    ";
		while (e != null) {
			sb.append(linePrefix).append(e.getClass().getName()).append(": ").append(e.getMessage()).append('\n');
			Arrays.stream(e.getStackTrace()).limit(4).forEach(stackTraceElement -> {
				sb.append(linePrefix).append(linePrefix).append(stackTraceElement.toString()).append('\n');
			});
			e = e.getCause();
		}
		return sb.toString();
	}


	public static String formatCodeBlock(String code) {
		return code.replaceAll("(?m)^(.)", "    $1").replaceFirst("\\s+$", "");
	}

	public static String pingForUser(String username) {
		return "@" + username.replace(" ", "");
	}

}
