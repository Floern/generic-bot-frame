/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame;

import com.floern.genericbot.frame.chat.ChatManager;
import com.floern.genericbot.frame.chat.commands.classes.Command;
import com.floern.genericbot.frame.utils.ProgramProperties;

import java.util.ArrayList;
import java.util.List;

public class GenericBot {


	private final ProgramProperties props;

	private ChatManager chatManager;

	private List<Command> addedCommands = new ArrayList<>();


	public GenericBot(ProgramProperties props) {
		this.props = props;
	}


	/**
	 * Add a command to the bot.
	 * @param command
	 */
	public GenericBot registerCommand(Command command) {
		if (chatManager != null) {
			chatManager.addCommand(command);
		}
		addedCommands.add(command);

		return this;
	}


	/**
	 * Start the bot.
	 */
	public final void start() {
		if (chatManager != null) {
			throw new IllegalStateException("GenericBot already running");
		}

		chatManager = new ChatManager(props);

		addedCommands.forEach(chatManager::addCommand);

		// start chat loop (blocking)
		chatManager.start(props.getProperty("chat.usermail"),
				props.getProperty("chat.userpass"),
				props.getInt("chat.devroomid"),
				props.getIntArray("chat.roomids"),
				this::onLine);

		onShutdown();
	}


	/**
	 * Pull the plug.
	 */
	public final void stop() {
		if (chatManager == null) {
			throw new IllegalStateException("GenericBot not running");
		}
		chatManager.terminate();
		chatManager = null;
	}


	/**
	 * Callback to setup everything the bot may need.
	 * Executed before the bot connects to the chat.
	 */
	protected void onStart() {
		// to be overridden
	}


	/**
	 * Callback when the bot is connected to all rooms.
	 */
	protected void onLine() {
		// to be overridden
	}


	/**
	 * Callback on termination.
	 */
	protected void onShutdown() {

	}


	/**
	 * Get the chat manager instance.
	 * @return
	 */
	public ChatManager getChatManager() {
		return chatManager;
	}


	/**
	 * Get the bot's properties.
	 * @return
	 */
	public ProgramProperties getProgramProperties() {
		return props;
	}

}
