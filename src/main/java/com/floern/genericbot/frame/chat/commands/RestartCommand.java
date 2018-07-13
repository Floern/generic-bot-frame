/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.chat.commands;

import com.floern.genericbot.frame.chat.ChatManager;
import com.floern.genericbot.frame.chat.commands.categories.MetaCommandCategory;
import com.floern.genericbot.frame.chat.commands.classes.PrivilegedCommand;

import org.sobotics.chatexchange.chat.Message;
import org.sobotics.chatexchange.chat.Room;

public class RestartCommand extends PrivilegedCommand implements MetaCommandCategory {


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
		chatManager.getDevChatRoom().send("restarting\u2026");
		chatManager.restart();
		return null;
	}


}
