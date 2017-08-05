/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.chat;

import com.floern.genericbot.frame.chat.commands.classes.Command;
import com.floern.genericbot.frame.utils.ProgramProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import fr.tunaki.stackoverflow.chat.ChatHost;
import fr.tunaki.stackoverflow.chat.Room;
import fr.tunaki.stackoverflow.chat.StackExchangeClient;
import fr.tunaki.stackoverflow.chat.event.EventType;
import fr.tunaki.stackoverflow.chat.event.MessageEvent;

public class ChatManager {

	private final static Logger LOGGER = LoggerFactory.getLogger(ChatManager.class);

	private final Collection<Long> botAdmins;

	private StackExchangeClient client;
	private ChatMessageHandler chatMessageHandler;

	private Room devChatroom;
	private Map<Integer, Room> chatrooms;

	private List<Command> availableCommands = new LinkedList<>();

	private Consumer<Room> onConnectedCallback;

	private CountDownLatch terminationSwitch;

	private volatile boolean isRestarting;


	public ChatManager(ProgramProperties programProperties) {
		this.botAdmins = Arrays.stream(programProperties.getIntArray("bot.admins"))
				.asLongStream().boxed().collect(Collectors.toList());
	}


	/**
	 * Start the chat manager, login to chat and wait for termination.
	 * Blocks until it's terminated.
	 * @param usermail Bot account mail.
	 * @param userpass Bot account password.
	 * @param devroomid Developer room ID.
	 * @param roomids All rooms to connect to.
	 * @param onConnectedCallback Callback when connected to a room.
	 * @param onAllConnectedCallback Callback when connected to all rooms.
	 */
	public void start(String usermail, String userpass, int devroomid, int[] roomids,
			Consumer<Room> onConnectedCallback, Runnable onAllConnectedCallback) {
		// conntect to chat
		this.client = new StackExchangeClient(usermail, userpass);
		this.chatMessageHandler = new ChatMessageHandler(this);
		this.chatrooms = new HashMap<>();
		this.onConnectedCallback = onConnectedCallback;

		// connect to dev room
		devChatroom = connectToRoom(ChatHost.STACK_OVERFLOW, devroomid);

		// conntect to other chatrooms
		for (int roomid : roomids) {
			if (roomid == devroomid)
				continue;
			connectToRoom(ChatHost.STACK_OVERFLOW, roomid);
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
	 * @param host Chat host.
	 * @param roomid Chat room ID.
	 */
	public Room connectToRoom(ChatHost host, int roomid) {
		LOGGER.info("connecting room " + roomid + "...");
		Room chatroom = client.joinRoom(host, roomid);
		chatroom.addEventListener(EventType.MESSAGE_POSTED, chatMessageHandler::process);
		chatroom.addEventListener(EventType.USER_MENTIONED, chatMessageHandler::process);
		chatroom.addEventListener(EventType.MESSAGE_REPLY, chatMessageHandler::process);
		chatrooms.put(roomid, chatroom);
		LOGGER.info("connected");
		onConnectedCallback.accept(chatroom);
		return chatroom;
	}


	/**
	 * Get the chatroom instance.
	 * @param roomId
	 * @return
	 */
	public Room getChatRoom(int roomId) {
		return chatrooms.getOrDefault(roomId, devChatroom);
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
	 * @return command, or null if none found.
	 */
	public Command getCommand(String command, MessageEvent invocationType) {
		for (Command cmd : availableCommands) {
			if (cmd.getInvocationType().equals(EventType.fromEvent(invocationType)) && cmd.is(command)) {
				return cmd;
			}
		}
		return null;
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

		String ret = command.invoke(this,
				message == null ? null : message.getRoom(),
				message == null ? null : message.getMessage(),
				messageContent.split("\\s+"));

		return new CommandResult(command.getResponseType(), ret);
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


	public Collection<Long> getBotAdmins() {
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
