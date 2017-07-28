/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.chat.commands;

import com.floern.genericbot.frame.chat.ChatManager;
import com.floern.genericbot.frame.chat.commands.classes.MetaCommandCategory;
import com.floern.genericbot.frame.chat.commands.classes.PrivilegedCommand;

import fr.tunaki.stackoverflow.chat.Message;
import fr.tunaki.stackoverflow.chat.Room;

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
		chatManager.getDevChatRoom().send("restarting...");
		chatManager.restart();
		return null;
	}


}
