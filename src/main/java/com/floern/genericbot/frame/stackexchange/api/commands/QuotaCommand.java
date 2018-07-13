/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.stackexchange.api.commands;

import com.floern.genericbot.frame.chat.ChatManager;
import com.floern.genericbot.frame.chat.commands.classes.Command;
import com.floern.genericbot.frame.stackexchange.api.net.ApiLoader;

import org.sobotics.chatexchange.chat.Message;
import org.sobotics.chatexchange.chat.Room;

public class QuotaCommand extends Command implements ApiCommandCategory {


	@Override
	public String[] getAliases() {
		return new String[] { "quota", "apiquota" };
	}


	@Override
	public String getUsageDescription() {
		return "show api quota info";
	}


	@Override
	protected String execute(ChatManager chatManager, Room chatroom, Message message, String[] args) {
		return "api quota remaining: " + ApiLoader.getQuotaRemaining() + " (max " + ApiLoader.getQuotaMax() + ")";
	}

}
