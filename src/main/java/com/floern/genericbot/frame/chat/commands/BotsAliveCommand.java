/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.chat.commands;

import com.floern.genericbot.frame.chat.ChatManager;
import com.floern.genericbot.frame.chat.commands.classes.Command;

import fr.tunaki.stackoverflow.chat.Message;
import fr.tunaki.stackoverflow.chat.Room;

public class BotsAliveCommand extends Command {


	@Override
	public boolean is(String command) {
		return command.matches("(?i)@bots\\s+alive\\b.*");
	}


	@Override
	public String[] getAliases() {
		return null;
	}


	@Override
	public String getUsageDescription() {
		return null;
	}


	@Override
	protected String execute(ChatManager chatManager, Room chatroom, Message message, String[] args) {
		return "aye";
	}

}
