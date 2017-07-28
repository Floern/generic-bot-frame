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
	 * @param file
	 */
	public static synchronized ProgramProperties load(String file) {
		ProgramProperties instance = new ProgramProperties();
		try {
			InputStream is = new FileInputStream(file);
			instance.load(is);
			is.close();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
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
		String[] parts = raw.split("(?<!\\\\),");
		int[] values = new int[parts.length];
		for (int i = 0; i < values.length; ++i) {
			values[i] = Integer.parseInt(parts[i]);
		}
		return values;
	}


}
