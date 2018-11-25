/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.chat;

import com.floern.genericbot.frame.chat.commands.classes.Command;
import com.floern.genericbot.frame.chat.model.ChatRoom;
import com.floern.genericbot.frame.chat.model.ChatUser;
import com.floern.genericbot.frame.utils.ProgramProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sobotics.chatexchange.chat.ChatHost;
import org.sobotics.chatexchange.chat.Room;
import org.sobotics.chatexchange.chat.StackExchangeClient;
import org.sobotics.chatexchange.chat.event.EventType;
import org.sobotics.chatexchange.chat.event.MessageEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ChatManager {

	private final static Logger LOGGER = LoggerFactory.getLogger(ChatManager.class);

	private final Collection<ChatUser> botAdmins;

	private StackExchangeClient client;
	private ChatMessageHandler chatMessageHandler;

	private Room devChatroom;
	private Map<ChatRoom, Room> chatrooms;

	private List<Command> availableCommands = new LinkedList<>();

	private Consumer<Room> onConnectedCallback;

	private CountDownLatch terminationSwitch;

	private volatile boolean isRestarting;


	public ChatManager(ProgramProperties programProperties) {
		this.botAdmins = Arrays.stream(programProperties.getStringArray("bot.admins"))
				.map(ChatUser::parse).collect(Collectors.toSet());
	}


	/**
	 * Start the chat manager, login to chat and wait for termination.
	 * Blocks until it's terminated.
	 * @param usermail Bot account mail.
	 * @param userpass Bot account password.
	 * @param devroom Developer room.
	 * @param rooms All rooms to connect to.
	 * @param onConnectedCallback Callback when connected to a room.
	 * @param onAllConnectedCallback Callback when connected to all rooms.
	 */
	public void start(String usermail, String userpass, ChatRoom devroom, List<ChatRoom> rooms,
			Consumer<Room> onConnectedCallback, Runnable onAllConnectedCallback) {
		// conntect to chat
		this.client = new StackExchangeClient(usermail, userpass);
		this.chatMessageHandler = new ChatMessageHandler(this);
		this.chatrooms = new HashMap<>();
		this.onConnectedCallback = onConnectedCallback;

		// connect to dev room
		devChatroom = connectToRoom(devroom);

		// conntect to other chatrooms
		for (ChatRoom room : rooms) {
			if (room.equals(devroom))
				continue;
			connectToRoom(room);
		}

		onAllConnectedCallback.run();

		terminationSwitch = new CountDownLatch(1);

		try {
			// wait for termination
			terminationSwitch.await();
			// wait to allow any pending chat messages being sent
			Thread.sleep(2000);
		}
		catch (InterruptedException e) {
			LOGGER.warn("terminationSwitch interrupted", e);
		}
		finally {
			client.close();
			client = null;
			chatrooms = null;
			devChatroom = null;
			LOGGER.info("disconnected");
		}
	}


	/**
	 * Connect to a chat room.
	 * @param room Chat room.
	 */
	public Room connectToRoom(ChatRoom room) {
		LOGGER.info("connecting room " + room.getRoomId() + " on " + room.getHost().getName() + "...");
		Room chatroom = client.joinRoom(room.getHost(), room.getRoomId());
		chatroom.addEventListener(EventType.MESSAGE_POSTED, chatMessageHandler::process);
		chatroom.addEventListener(EventType.USER_MENTIONED, chatMessageHandler::process);
		chatroom.addEventListener(EventType.MESSAGE_REPLY, chatMessageHandler::process);
		chatrooms.put(room, chatroom);
		LOGGER.info("connected");
		onConnectedCallback.accept(chatroom);
		return chatroom;
	}


	/**
	 * Get the chatroom instance.
	 * @param roomId
	 * @return
	 */
	public Room getChatRoom(ChatHost host, int roomId) {
		return chatrooms.getOrDefault(new ChatRoom(roomId, host), devChatroom);
	}


	/**
	 * Get the chatroom instance.
	 * @param room
	 * @return
	 */
	public Room getChatRoom(ChatRoom room) {
		return chatrooms.getOrDefault(room, devChatroom);
	}


	/**
	 * Get the chatroom instance.
	 * @return
	 */
	public Room getDevChatRoom() {
		return devChatroom;
	}


	/**
	 * Get all chatrooms
	 * @return
	 */
	public List<Room> getAllRooms() {
		List<Room> rooms = new LinkedList<>();
		rooms.add(devChatroom);
		rooms.addAll(chatrooms.values());
		return rooms;
	}


	/**
	 * Add a new chat command.
	 * @param command
	 */
	public void addCommand(Command command) {
		availableCommands.add(command);
	}


	/**
	 * Get all registered commands.
	 * @return
	 */
	public List<Command> getAllCommands() {
		return availableCommands;
	}


	/**
	 * Get the matching command for the input command string.
	 * @param command input string
	 * @param invocationType
	 * @return list of matching commands, or empty if none found.
	 */
	public List<Command> getCommands(String command, MessageEvent invocationType) {
		return availableCommands.stream()
				.filter(cmd -> cmd.getInvocationType().equals(EventType.fromEvent(invocationType)) && cmd.is(command))
				.collect(Collectors.toList());
	}


	/**
	 * Execute a command.
	 * @param command Command instance.
	 * @param message Chat message event that triggered this command, null if it's not from chat.
	 * @param messageContent plain command message string.
	 * @return Command result.
	 */
	public CommandResult executeCommand(Command command, MessageEvent message, String messageContent) {
		LOGGER.info("command: " + command.getClass().getSimpleName());

		try {
			String ret = command.invoke(this,
					message == null ? null : message.getRoom(),
					message == null ? null : message.getMessage(),
					messageContent.split("\\s+"));

			return new CommandResult(command.getResponseType(), ret);
		}
		catch (Exception e) {
			LOGGER.error("command", e);
			return null;
		}
	}


	/**
	 * Trigger a restart, i.e. log off the chat and shutdown (and wait for someone to start a new instance again).
	 */
	public void restart() {
		LOGGER.info("restarting...");
		isRestarting = true;
		terminationSwitch.countDown();
	}


	public boolean isRestarting() {
		return isRestarting;
	}


	public Collection<ChatUser> getBotAdmins() {
		return botAdmins;
	}


	/**
	 * Trigger termination.
	 */
	public void terminate() {
		isRestarting = false;
		terminationSwitch.countDown();
	}


	public static class CommandResult {
		private final Command.ResponseType responseType;
		private final String message;

		public CommandResult(Command.ResponseType responseType, String message) {
			this.responseType = responseType;
			this.message = message;
		}

		public Command.ResponseType getResponseType() {
			return responseType;
		}

		public String getMessage() {
			return message;
		}
	}

}
