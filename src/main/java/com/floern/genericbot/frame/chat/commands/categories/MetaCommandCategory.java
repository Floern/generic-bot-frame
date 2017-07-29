package com.floern.genericbot.frame.chat.commands.categories;

public interface MetaCommandCategory extends CommandCategory {

	default String getCommandCategory() {
		return "meta";
	}

}
