/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.utils;

import java.util.HashMap;
import java.util.Map;

public class MapUtil {


	public static <K, V> Map<K, V> createSingle(K key, V value) {
		Map<K, V> map = new HashMap<K, V>();
		map.put(key, value);
		return map;
	}


}
