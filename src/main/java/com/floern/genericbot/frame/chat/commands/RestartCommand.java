/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.chat.commands;

import com.floern.genericbot.frame.chat.ChatManager;
import com.floern.genericbot.frame.chat.commands.classes.PrivilegedCommand;

import fr.tunaki.stackoverflow.chat.Message;
import fr.tunaki.stackoverflow.chat.Room;

public class RestartCommand extends PrivilegedCommand {


	@Override
	public String[] getAliases() {
		return new String[] { "restart", "reboot" };
	}


	@Override
	public String getUsageDescription() {
		return "restart the bot (privileged command)";
	}


	@Override
	protected String execute(ChatManager chatManager, Room chatroom, Message message, String[] args) {
		if (chatroom != null) {
			chatroom.send("legen\u2026");
			if (chatroom.getRoomId() != chatManager.getDevChatRoom().getRoomId()) {
				chatManager.getDevChatRoom().send("legen\u2026");
			}
			chatManager.restart(chatroom.getRoomId());
		}
		else {
			chatManager.getDevChatRoom().send("restarting...");
			chatManager.restart(-1);
		}
		return null;
	}


}
