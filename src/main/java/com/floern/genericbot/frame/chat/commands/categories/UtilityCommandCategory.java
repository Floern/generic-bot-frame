package com.floern.genericbot.frame.chat.commands.categories;

public interface UtilityCommandCategory extends CommandCategory {

	default String getCommandCategory() {
		return "utility";
	}

}
