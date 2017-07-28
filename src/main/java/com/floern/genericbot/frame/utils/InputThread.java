/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.utils;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Thread that reads from System.in
 */
public class InputThread extends Thread {

	private static Set<OnLineReadListener> listeners = new HashSet<>();

	private static InputThread instance;


	public static synchronized void go() {
		if (instance == null) {
			instance = new InputThread();
			instance.setDaemon(true);
			instance.start();
		}
	}


	private InputThread() {
		super("InputThread");
	}


	@Override
	public void run() {
		Scanner input = new Scanner(System.in);
		while (input.hasNextLine()) {
			String line = input.nextLine();
			synchronized (InputThread.class) {
				listeners.forEach(onLineReadListener -> onLineReadListener.onLineRead(line));
			}
		}
	}


	public static synchronized void addOnLineReadListener(OnLineReadListener listener) {
		listeners.add(listener);
	}


	public static synchronized void removeOnLineReadListener(OnLineReadListener listener) {
		listeners.remove(listener);
	}


	public interface OnLineReadListener {
		void onLineRead(String line);
	}

}
