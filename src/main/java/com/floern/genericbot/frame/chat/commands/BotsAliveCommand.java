/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.chat.commands;

import com.floern.genericbot.frame.chat.ChatManager;
import com.floern.genericbot.frame.chat.commands.classes.Command;

import org.sobotics.chatexchange.chat.Message;
import org.sobotics.chatexchange.chat.Room;
import org.sobotics.chatexchange.chat.event.EventType;
import org.sobotics.chatexchange.chat.event.MessageEvent;

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


	@Override
	public EventType<? extends MessageEvent> getInvocationType() {
		return EventType.MESSAGE_POSTED;
	}

}
