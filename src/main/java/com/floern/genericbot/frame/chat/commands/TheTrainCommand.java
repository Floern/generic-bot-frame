/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.chat.commands;

import com.floern.genericbot.frame.chat.ChatManager;
import com.floern.genericbot.frame.chat.commands.classes.Command;
import com.floern.genericbot.frame.utils.Emojis;

import fr.tunaki.stackoverflow.chat.Message;
import fr.tunaki.stackoverflow.chat.Room;

public class TheTrainCommand extends Command {


	@Override
	public boolean is(String command) {
		int firstCodePoint = Character.codePointAt(command, 0);
		return (firstCodePoint == 128642 || (128644 <= firstCodePoint && firstCodePoint <= 128650));
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
		return Emojis.TRAIN_WAGGON;
	}

}
