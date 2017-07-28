/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.chat.commands;

import com.floern.genericbot.frame.chat.ChatManager;
import com.floern.genericbot.frame.chat.commands.classes.AdminCommand;
import com.floern.genericbot.frame.chat.commands.classes.MetaCommandCategory;

import fr.tunaki.stackoverflow.chat.Message;
import fr.tunaki.stackoverflow.chat.Room;

public class TerminateCommand extends AdminCommand implements MetaCommandCategory {


	@Override
	public String[] getAliases() {
		return new String[] { "stop", "terminate", "shutdown", "leave", "stahp", "go away", "fuck off" };
	}


	@Override
	public String getUsageDescription() {
		return "stop the world (admin command)";
	}


	@Override
	protected String execute(ChatManager chatManager, Room chatroom, Message message, String[] args) {
		if (chatroom == null || chatroom.getRoomId() != chatManager.getDevChatRoom().getRoomId()) {
			chatManager.getDevChatRoom().send("over and out");
		}
		chatManager.terminate();
		return "over and out";
	}


}
