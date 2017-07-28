/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.chat;

import com.floern.genericbot.frame.chat.commands.classes.Command;
import com.floern.genericbot.frame.utils.ProgramProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import fr.tunaki.stackoverflow.chat.ChatHost;
import fr.tunaki.stackoverflow.chat.Room;
import fr.tunaki.stackoverflow.chat.StackExchangeClient;
import fr.tunaki.stackoverflow.chat.event.EventType;
import fr.tunaki.stackoverflow.chat.event.MessageEvent;

public class ChatManager {

	private final static Logger LOGGER = LoggerFactory.getLogger(ChatManager.class);

	private final ProgramProperties programProperties;

	private final List<Long> botOwners;

	private Room devChatroom;
	private Map<Integer, Room> chatrooms;

	private List<Command> availableCommands = new LinkedList<>();

	private CountDownLatch terminationCountdown;

	private volatile boolean isRestarting;


	public ChatManager(ProgramProperties programProperties) {
		this.programProperties = programProperties;

		this.botOwners = Arrays.stream(programProperties.getIntArray("chat.roomids"))
				.asLongStream().boxed().collect(Collectors.toList());
	}


	/**
	 * Start the chat manager, login to chat and wait for termination.
	 * Blocks until it's terminated.
	 * @param usermail
	 * @param userpass
	 * @param devroomid
	 * @param roomids
	 * @param chatConnectedCallback
	 */
	public void start(String usermail, String userpass, int devroomid, int[] roomids, Runnable chatConnectedCallback) {
		terminationCountdown = new CountDownLatch(1);

		// conntect to chat
		StackExchangeClient client = new StackExchangeClient(usermail, userpass);
		ChatMessageHandler chatMessageHandler = new ChatMessageHandler(this);

		// connect to dev room
		LOGGER.info("connecting dev room...");
		devChatroom = client.joinRoom(ChatHost.STACK_OVERFLOW, devroomid);
		devChatroom.addEventListener(EventType.MESSAGE_POSTED, chatMessageHandler::process);
		devChatroom.addEventListener(EventType.USER_MENTIONED, chatMessageHandler::process);
		devChatroom.addEventListener(EventType.MESSAGE_REPLY, chatMessageHandler::process);
		LOGGER.info("connected");

		// conntect to other chatrooms
		chatrooms = new HashMap<>();
		for (int roomid : roomids) {
			if (roomid == devroomid)
				continue;
			LOGGER.info("connecting room " + roomid + "...");
			Room chatroom = client.joinRoom(ChatHost.STACK_OVERFLOW, roomid);
			chatroom.addEventListener(EventType.MESSAGE_POSTED, chatMessageHandler::process);
			chatroom.addEventListener(EventType.USER_MENTIONED, chatMessageHandler::process);
			chatroom.addEventListener(EventType.MESSAGE_REPLY, chatMessageHandler::process);
			chatrooms.put(roomid, chatroom);
			LOGGER.info("connected");
		}

		devChatroom.send("up and online!");

		chatConnectedCallback.run();

		try {
			// wait for termination
			terminationCountdown.await();
			Thread.sleep(2000);
		}
		catch (InterruptedException e) {
			LOGGER.debug("terminationCountdown interrupted", e);
		}
		finally {
			client.close();
			chatrooms = null;
			devChatroom = null;
			LOGGER.info("disconnected");
		}
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
		LOGGER.info("command: " + command);

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
		isRestarting = true;
		terminationCountdown.countDown();
	}


	public boolean isRestarting() {
		return isRestarting;
	}


	public List<Long> getBotOwners() {
		return botOwners;
	}


	/**
	 * Trigger termination.
	 */
	public void terminate() {
		terminationCountdown.countDown();
	}


	public ProgramProperties getProgramProperties() {
		return programProperties;
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
