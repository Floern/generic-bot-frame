/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.utils;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

public class ChatPrinter {


	private static final List<String> INTERNAL_PACKAGE_PREFIXES =
			Lists.newArrayList("org.apache.http.", "java.net.", "sun.security.ssl.");


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


	public static String formatExceptionCondensed(Throwable e) {
		StringBuilder sb = new StringBuilder(512);
		String linePrefix = "    ";
		while (e != null) {
			sb.append(linePrefix).append(e.getClass().getName()).append(": ").append(e.getMessage()).append('\n');
			String prevPackagePrefix = "~";
			for (StackTraceElement stackTraceElement : e.getStackTrace()) {
				if (stackTraceElement.getClassName().startsWith(prevPackagePrefix))
					continue;
				prevPackagePrefix = INTERNAL_PACKAGE_PREFIXES.stream()
						.filter(prefix -> stackTraceElement.getClassName().startsWith(prefix))
						.findAny().orElse(stackTraceElement.getClassName());
				String element = stackTraceElement.toString().replaceAll("(\\.[a-z_]{3})\\w{2,}(?=\\.)", "$1~");
				sb.append(linePrefix).append(linePrefix).append(element).append('\n');
			}
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
