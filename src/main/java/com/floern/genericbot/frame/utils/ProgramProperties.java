/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ProgramProperties extends Properties {


	/**
	 * Load a new property file.
	 * @param files list of property files
	 */
	public static synchronized ProgramProperties load(String... files) {
		ProgramProperties instance = new ProgramProperties();
		for (String file : files) {
			try (InputStream is = new FileInputStream(file)) {
				instance.load(is);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return instance;
	}


	public boolean getBoolean(String key) {
		return Boolean.parseBoolean(getProperty(key));
	}


	public boolean getBoolean(String key, boolean defaultValue) {
		return Boolean.parseBoolean(getProperty(key, Boolean.toString(defaultValue)));
	}


	public int getInt(String key) {
		return Integer.parseInt(getProperty(key));
	}


	public int getInt(String key, int defaultValue) {
		return Integer.parseInt(getProperty(key, Integer.toString(defaultValue)));
	}


	public int[] getIntArray(String key) {
		String raw = getProperty(key);
		if (StringUtil.isEmpty(raw)) {
			return new int[0];
		}
		String[] parts = raw.split("(?<!\\\\),");
		int[] values = new int[parts.length];
		for (int i = 0; i < values.length; ++i) {
			values[i] = Integer.parseInt(parts[i].trim());
		}
		return values;
	}


	public String[] getStringArray(String key) {
		String raw = getProperty(key);
		if (StringUtil.isEmpty(raw)) {
			return new String[0];
		}
		String[] parts = raw.split("(?<!\\\\),");
		for (int i = 0; i < parts.length; i++) {
			parts[i] = parts[i].trim();
		}
		return parts;
	}

}
