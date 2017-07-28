/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.stackexchange.api.commands;

import com.floern.stackoverflow.chat.ChatManager;
import com.floern.stackoverflow.core.commands.classes.Command;
import com.floern.stackoverflow.se_api.net.ApiLoader;

import fr.tunaki.stackoverflow.chat.Message;
import fr.tunaki.stackoverflow.chat.Room;

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
