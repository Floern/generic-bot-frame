package com.floern.genericbot.frame.chat.commands.classes;

public interface MetaCommandCategory extends CommandCategory {

	default String getCommandCategory() {
		return "meta";
	}

}
