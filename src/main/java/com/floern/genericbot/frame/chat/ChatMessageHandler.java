/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.chat;

import com.floern.genericbot.frame.chat.commands.classes.Command;
import com.floern.genericbot.frame.utils.ChatPrinter;
import com.floern.genericbot.frame.utils.RateLimiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.tunaki.stackoverflow.chat.Message;
import fr.tunaki.stackoverflow.chat.User;
import fr.tunaki.stackoverflow.chat.event.MessageEvent;
import fr.tunaki.stackoverflow.chat.event.MessagePostedEvent;
import fr.tunaki.stackoverflow.chat.event.MessageReplyEvent;
import fr.tunaki.stackoverflow.chat.event.UserMentionedEvent;

class ChatMessageHandler {

	private final static Logger LOGGER = LoggerFactory.getLogger(ChatMessageHandler.class);

	private final String selfPing;

	private final ChatManager chatManager;

	private final Map<Long, RateLimiter> perUserRateLimit = new HashMap<>();


	protected ChatMessageHandler(ChatManager chatManager) {
		this.chatManager = chatManager;
		this.selfPing = ChatPrinter.pingForUser(chatManager.getProgramProperties().getProperty("chat.username", "$@#~"));
	}


	protected void process(MessagePostedEvent messageEvent) {
		String messageContent = messageEvent.getMessage().getPlainContent();
		process(messageEvent, messageContent);
	}


	protected void process(MessageReplyEvent messageEvent) {
		String messageContent = messageEvent.getMessage().getPlainContent().replaceFirst("^:\\d+ ", "").trim();
		process(messageEvent, messageContent);
	}


	protected void process(UserMentionedEvent messageEvent) {
		String messageContent = messageEvent.getMessage().getPlainContent().trim();
		if (!selfPing.toLowerCase().startsWith(messageContent.split("\\s+")[0].toLowerCase()))
			return;

		messageContent = messageContent.replaceFirst("^@\\w+\\b", "").trim();

		process(messageEvent, messageContent);
	}


	private void process(MessageEvent messageEvent, String messageContent) {
		Message message = messageEvent.getMessage();

		Command command = chatManager.getCommand(messageContent, messageEvent);
		if (command == null) {
			// no command found
			return;
		}

		if (isRateLimited(messageEvent)) {
			return;
		}

		ChatManager.CommandResult ret = chatManager.executeCommand(command, messageEvent, messageContent);

		if (ret != null && ret.getMessage() != null) {
			switch (ret.getResponseType()) {
				case NORMAL:
					messageEvent.getRoom().send(ret.getMessage());
					break;
				case REPLY:
					messageEvent.getRoom().replyTo(message.getId(), ret.getMessage());
					break;
				case NONE:
					// nothing
					break;
			}
		}
	}


	private boolean isRateLimited(MessageEvent message) {
		if (message == null || !message.getUser().isPresent()) {
			return true;
		}

		User user = message.getUser().get();
		long userId = user.getId();
		if (!chatManager.getBotOwners().contains(userId)) {
			if (perUserRateLimit.containsKey(userId)) {
				RateLimiter rateLimiter = perUserRateLimit.get(userId);
				rateLimiter.addNewRequestNow();
				if (rateLimiter.isBlocked()) {
					LOGGER.info("Request of user " + user.getName() + " has been dropped due to their rate limit");
					if (rateLimiter.sendNotice()) {
						message.getRoom().send("*ignores " + user.getName() + "*");
					}
					return true;
				}
			}
			else {
				boolean userIsPriv = user.isRoomOwner() || user.isModerator();
				perUserRateLimit.put(userId, new RateLimiter(userIsPriv ? 10 : 4, 45, TimeUnit.SECONDS, true));
			}
		}

		return false;
	}

}
