/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.chat.commands.classes;

import com.floern.genericbot.frame.chat.ChatManager;

import fr.tunaki.stackoverflow.chat.User;

/**
 * A command that can only be executed by room owners or moderators.
 */
public abstract class PrivilegedCommand extends Command {

	/**
	 * Check whether a User is privileged to execute this command.
	 * @param user User that wants to execute this command.
	 * @return
	 */
	@Override
	public boolean isCommanderPrivileged(ChatManager chatManager, User user) {
		return user.isRoomOwner() || user.isModerator() || chatManager.getBotOwners().contains(user.getId());
	}

}
