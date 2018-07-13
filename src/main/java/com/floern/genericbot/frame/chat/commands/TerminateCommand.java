/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.chat.commands;

import com.floern.genericbot.frame.chat.ChatManager;
import com.floern.genericbot.frame.chat.commands.classes.AdminCommand;
import com.floern.genericbot.frame.chat.commands.categories.MetaCommandCategory;

import org.sobotics.chatexchange.chat.Message;
import org.sobotics.chatexchange.chat.Room;

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
