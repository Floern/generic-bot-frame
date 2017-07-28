package com.floern.genericbot.frame.chat.commands.classes;

public interface UtilityCommandCategory extends CommandCategory {

	default String getCommandCategory() {
		return "utilities";
	}

}
