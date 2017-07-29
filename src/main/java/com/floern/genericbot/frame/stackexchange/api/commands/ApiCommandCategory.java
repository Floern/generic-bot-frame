/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.stackexchange.api.commands;

import com.floern.genericbot.frame.chat.commands.categories.CommandCategory;

public interface ApiCommandCategory extends CommandCategory {

	default String getCommandCategory() {
		return "stackexchange api";
	}

}
