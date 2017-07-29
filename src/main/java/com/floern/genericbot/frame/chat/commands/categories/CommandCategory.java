package com.floern.genericbot.frame.chat.commands.categories;

public interface CommandCategory {

	default String getCommandCategory() {
		return "generic";
	}

}
