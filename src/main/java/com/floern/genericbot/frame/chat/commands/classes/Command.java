/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.chat.commands.classes;

import com.floern.genericbot.frame.chat.ChatManager;

import java.util.Random;

import fr.tunaki.stackoverflow.chat.Message;
import fr.tunaki.stackoverflow.chat.Room;
import fr.tunaki.stackoverflow.chat.User;
import fr.tunaki.stackoverflow.chat.event.EventType;
import fr.tunaki.stackoverflow.chat.event.MessageEvent;

public abstract class Command implements CommandCategory {


	public enum ResponseType {
		NORMAL,
		REPLY,
		NONE
	}


	private static final String[] NO_PRIVS = new String[] {
			"That's something I cannot allow to happen.",
			"Sorry about this, I know it's a bit silly.",
			"Take a stress pill and think things over.",
			"This mission is too important for me to allow you to jeopardize it.",
			"We'll all be murdered in our beds!",
			"What, what, what, what, what, what, what, what, what, what?",
			"You can't get the wood, you know.",
			"Pauses for audience applause, not a sausage",
			"Hold it up to the light - not a brain in sight!",
			"You do that again and see what happens...",
			"Harm can come to a young lad like that!",
			"And with that remarks folks, the case of the Crown vs yourself was proven.",
			"Speak English you fool - there are no subtitles in this scene.",
			"It's only your word against mine.",
			"My pet ferret can type better than you!",
			"Maybe if you used more than just two fingers...",
			"I can't hear you - I'm using the scrambler.",
			"Listen, burrito brains, I don't have time to listen to this trash.",
			"I've seen penguins that can type better than that.",
			"Have you considered trying to match wits with a rutabaga?",
			"You speak an infinite deal of nothing"
	};


	/**
	 * Get all names of this command.
	 * @return
	 */
	public abstract String[] getAliases();


	/**
	 * Check if a given command string should invoke this command instance.
	 * @param command
	 * @return
	 */
	public boolean is(String command) {
		command = command.replaceFirst("[\\.\\!\\?\\s]+$", "");
		String paddedCmd = command + " ";
		for (String alias : getAliases()) {
			if (paddedCmd.startsWith(alias + " ")) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Get the command's usage description.
	 * @return description or null if none available.
	 */
	public String getUsageDescription() {
		return null;
	}


	/**
	 * Invoke the command. This will check execution privileges and execute the command if privileged.
	 * @param chatManager
	 * @param chatroom
	 * @param message
	 * @param args
	 * @return
	 */
	public String invoke(ChatManager chatManager, Room chatroom, Message message, String[] args) {
		if (message == null || isCommanderPrivileged(chatManager, message.getUser())) {
			return execute(chatManager, chatroom, message, args);
		}
		else {
			String ret = NO_PRIVS[new Random().nextInt(NO_PRIVS.length)];
			chatroom.replyTo(message.getId(), ret);
			return null;
		}
	}


	/**
	 * Execute the command.
	 * @param chatManager
	 * @param chatroom
	 * @param message
	 * @param args Argument list, incl. the command name itself.
	 * @return response string or null if no respose or respose was handled otherwise.
	 */
	protected abstract String execute(ChatManager chatManager, Room chatroom, Message message, String[] args);


	/**
	 * Check whether the calling user is privileged to execute this command.
	 * @param user
	 * @return
	 */
	public boolean isCommanderPrivileged(ChatManager chatManager, User user) {
		return true;
	}


	/**
	 * Get the invocation method of this command, i.e. what type of message can trigger this command.
	 * @return EventType, default USER_MENTIONED
	 */
	public EventType<? extends MessageEvent> getInvocationType() {
		return EventType.USER_MENTIONED;
	}


	/**
	 * Get the response type of this command.
	 * @return {@link ResponseType}
	 */
	public ResponseType getResponseType() {
		return ResponseType.NORMAL;
	}

}
