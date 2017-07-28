/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.chat;

import com.floern.genericbot.frame.chat.commands.*;
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
	private StatusCommand statusCommand;

	private CountDownLatch terminationCountdown;

	private volatile boolean isRestarting;
	private volatile int restartRoomId;


	public ChatManager(ProgramProperties programProperties) {
		this(programProperties, -1);
	}


	public ChatManager(ProgramProperties programProperties, int restartRoomId) {
		this.programProperties = programProperties;
		this.restartRoomId = restartRoomId;

		this.botOwners = Arrays.stream(programProperties.getIntArray("chat.roomids"))
				.asLongStream().boxed().collect(Collectors.toList());

		availableCommands.clear();

		// generic commands
		availableCommands.add(new AliveCommand());
		statusCommand = new StatusCommand();
		availableCommands.add(statusCommand);
		availableCommands.add(new RestartCommand());
		availableCommands.add(new TerminateCommand());
		availableCommands.add(new CommandsCommand(availableCommands));

		availableCommands.add(new BotsAliveCommand());
		availableCommands.add(new TheTrainCommand());
	}


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

		if (restartRoomId > 0) {
			devChatroom.send("\u2026dary");
			if (restartRoomId != devroomid && chatrooms.containsKey(restartRoomId)) {
				chatrooms.get(restartRoomId).send("\u2026dary");
			}
		}
		else {
			devChatroom.send("up and online!");
		}

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
		return chatrooms.containsKey(roomId) ? chatrooms.get(roomId) : devChatroom;
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
	 * Add a new status record callback.
	 * @param statusRecordCallback
	 */
	public void registerStatusRecordCallback(StatusCommand.StatusRecordCallback statusRecordCallback) {
		statusCommand.registerStatusRecordCallback(statusRecordCallback);
	}


	/**
	 * Get the matching command for the input command string.
	 * @param command
	 * @param invocationType
	 * @return command, or null if none found.
	 */
	public Command getCommand(String command, int invocationType) {
		for (Command cmd : availableCommands) {
			if ((cmd.getInvocationType() & invocationType) != 0 && cmd.is(command)) {
				return cmd;
			}
		}
		return null;
	}


	/**
	 * Execute a command.
	 * @param command Command instance.
	 * @param message Chat message event, null if it's not from chat.
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
	 * @param roomID the ID of the room from that the restart was requested.
	 */
	public void restart(int roomID) {
		isRestarting = true;
		restartRoomId = roomID;
		terminationCountdown.countDown();
	}


	/**
	 * Trigger a restart, i.e. log off the chat and shutdown (and wait for someone to start a new instance again).
	 */
	public void restart() {
		restart(-1);
	}


	public boolean isRestarting() {
		return isRestarting;
	}


	public int getRestartRoomId() {
		return restartRoomId;
	}


	public List<Long> getBotOwners() {
		return botOwners;
	}


	/**
	 * Trigger termination.
	 */
	public void terminate() {
		restartRoomId = -1;
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
