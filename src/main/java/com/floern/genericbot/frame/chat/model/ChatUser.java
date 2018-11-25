/*
 * Floern, dev@floern.com, 2018, MIT Licence
 */
package com.floern.genericbot.frame.chat.model;

import org.sobotics.chatexchange.chat.ChatHost;

import java.util.Objects;

public class ChatUser {

	private final long userId;
	private final ChatHost host;

	/**
	 * Get a {@link ChatUser} instance from a String with the format "{@code host.userid}".
	 * @param spec
	 * @return
	 */
	public static ChatUser parse(String spec) {
		if (spec == null || !spec.matches("\\w+\\.\\d+")) {
			throw new IllegalArgumentException("Invalid chatroom spec: " + spec);
		}
		String[] parts = spec.split("\\.", 2);
		long userId = Long.parseLong(parts[1]);
		ChatHost host;
		switch (parts[0]) {
			case "so": host = ChatHost.STACK_OVERFLOW; break;
			case "se": host = ChatHost.STACK_EXCHANGE; break;
			case "mse": host = ChatHost.META_STACK_EXCHANGE; break;
			default: throw new IllegalArgumentException("Unknown chat host: " + parts[0]);
		}
		return new ChatUser(userId, host);
	}

	public ChatUser(long userId, ChatHost host) {
		this.userId = userId;
		this.host = host;
	}

	public ChatHost getHost() {
		return host;
	}

	public long getUserId() {
		return userId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ChatUser chatRoom = (ChatUser) o;
		return userId == chatRoom.userId &&
				host == chatRoom.host;
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId, host);
	}
}
