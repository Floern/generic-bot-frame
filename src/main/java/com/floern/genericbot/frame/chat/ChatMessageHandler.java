/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.chat;

import com.floern.genericbot.frame.chat.commands.classes.Command;
import com.floern.genericbot.frame.chat.model.ChatUser;
import com.floern.genericbot.frame.utils.ChatPrinter;
import com.floern.genericbot.frame.utils.RateLimiter;
import org.sobotics.chatexchange.chat.Message;
import org.sobotics.chatexchange.chat.User;
import org.sobotics.chatexchange.chat.event.MessageEvent;
import org.sobotics.chatexchange.chat.event.MessagePostedEvent;
import org.sobotics.chatexchange.chat.event.MessageReplyEvent;
import org.sobotics.chatexchange.chat.event.UserMentionedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

class ChatMessageHandler {

	private final static Logger LOGGER = LoggerFactory.getLogger(ChatMessageHandler.class);

	private final ChatManager chatManager;

	private final Map<ChatUser, RateLimiter> perUserRateLimit = new HashMap<>();


	protected ChatMessageHandler(ChatManager chatManager) {
		this.chatManager = chatManager;
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

		String ownName = messageEvent.getRoom().getUser(messageEvent.getTargetUserId()).getName();
		String selfPing = ChatPrinter.pingForUser(ownName);
		if (!selfPing.toLowerCase().startsWith(messageContent.split("\\s+")[0].toLowerCase()))
			return;

		messageContent = messageContent.replaceFirst("^@\\w+\\b", "").trim();

		process(messageEvent, messageContent);
	}


	private void process(MessageEvent messageEvent, String messageContent) {
		Message message = messageEvent.getMessage();

		List<Command> commands = chatManager.getCommands(messageContent, messageEvent);
		if (commands.isEmpty()) {
			// no command found
			return;
		}

		if (isRateLimited(messageEvent)) {
			return;
		}

		for (Command command : commands) {
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
	}


	private boolean isRateLimited(MessageEvent message) {
		if (message == null || !message.getUser().isPresent()) {
			return true;
		}

		User user = message.getUser().get();
		long userId = user.getId();
		ChatUser userSpec = new ChatUser(userId, message.getRoom().getHost());
		if (!chatManager.getBotAdmins().contains(userSpec)) {
			if (perUserRateLimit.containsKey(userSpec)) {
				RateLimiter rateLimiter = perUserRateLimit.get(userSpec);
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
				boolean userIsPriv = user.isRoomOwner() || user.isModerator() || chatManager.getBotAdmins().contains(userSpec);
				perUserRateLimit.put(userSpec, new RateLimiter(userIsPriv ? 10 : 4, 45, TimeUnit.SECONDS, true));
			}
		}

		return false;
	}

}
