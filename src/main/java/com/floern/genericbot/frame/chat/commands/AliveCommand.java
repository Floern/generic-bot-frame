/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.chat.commands;

import com.floern.genericbot.frame.chat.ChatManager;
import com.floern.genericbot.frame.chat.commands.classes.Command;
import com.floern.genericbot.frame.chat.commands.categories.MetaCommandCategory;

import org.sobotics.chatexchange.chat.Message;
import org.sobotics.chatexchange.chat.Room;

public class AliveCommand extends Command implements MetaCommandCategory {


	@Override
	public String[] getAliases() {
		return new String[] { "alive" };
	}


	@Override
	public String getUsageDescription() {
		return "ping the bot";
	}


	@Override
	protected String execute(ChatManager chatManager, Room chatroom, Message message, String[] args) {
		return "aye";
	}


	@Override
	public ResponseType getResponseType() {
		return ResponseType.REPLY;
	}

}
