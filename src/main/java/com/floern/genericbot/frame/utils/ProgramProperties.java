/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ProgramProperties extends Properties {


	private static String propertyFile;
	private static ProgramProperties instance;


	/**
	 * Get the Properties instance.
	 * @return
	 */
	public static synchronized ProgramProperties i() {
		return instance;
	}


	/**
	 * Load a new property file.
	 * @param file
	 */
	public static synchronized void load(String file) {
		propertyFile = file;
		instance = new ProgramProperties();
		try {
			InputStream is = new FileInputStream(file);
			instance.load(is);
			is.close();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * Reload the property file.
	 */
	public static void reload() {
		load(propertyFile);
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
