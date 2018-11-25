/*
 * Floern, dev@floern.com, 2018, MIT Licence
 */
package com.floern.genericbot.frame.chat.model;

import org.sobotics.chatexchange.chat.ChatHost;
import org.sobotics.chatexchange.chat.Room;

import java.util.Objects;

public class ChatRoom {

	private final int roomId;
	private final ChatHost host;

	/**
	 * Get a {@link ChatRoom} instance from a String with the format "{@code host.roomid}".
	 * @param spec
	 * @return
	 */
	public static ChatRoom parse(String spec) {
		if (spec == null || !spec.matches("\\w+\\.\\d+")) {
			throw new IllegalArgumentException("Invalid chatroom spec: " + spec);
		}
		String[] parts = spec.split("\\.", 2);
		int roomId = Integer.parseInt(parts[1]);
		ChatHost host;
		switch (parts[0]) {
			case "so": host = ChatHost.STACK_OVERFLOW; break;
			case "se": host = ChatHost.STACK_EXCHANGE; break;
			case "mse": host = ChatHost.META_STACK_EXCHANGE; break;
			default: throw new IllegalArgumentException("Unknown chat host: " + parts[0]);
		}
		return new ChatRoom(roomId, host);
	}

	public static ChatRoom fromRoom(Room room) {
		return new ChatRoom(room.getRoomId(), room.getHost());
	}

	public ChatRoom(int roomId, ChatHost host) {
		this.roomId = roomId;
		this.host = host;
	}

	public ChatHost getHost() {
		return host;
	}

	public int getRoomId() {
		return roomId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ChatRoom chatRoom = (ChatRoom) o;
		return roomId == chatRoom.roomId &&
				host == chatRoom.host;
	}

	@Override
	public int hashCode() {
		return Objects.hash(roomId, host);
	}

}
