package com.floern.genericbot.frame.chat.commands.classes;

public interface CommandCategory {

	default String getCommandCategory() {
		return "generic";
	}

}
