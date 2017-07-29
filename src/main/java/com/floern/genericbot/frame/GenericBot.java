/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame;

import com.floern.genericbot.frame.chat.ChatManager;
import com.floern.genericbot.frame.chat.commands.classes.Command;
import com.floern.genericbot.frame.redunda.RedundaService;
import com.floern.genericbot.frame.utils.ProgramProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import fr.tunaki.stackoverflow.chat.Room;

public class GenericBot {

	private static final Logger LOGGER = LoggerFactory.getLogger(GenericBot.class);

	private static final String PROP_KEY_CHAT_USERMAIL = "chat.usermail";
	private static final String PROP_KEY_CHAT_USERPASS = "chat.userpass";
	private static final String PROP_KEY_CHAT_DEVROOM = "chat.devroomid";
	private static final String PROP_KEY_CHAT_ROOMS = "chat.roomids";
	private static final String PROP_KEY_REDUNDA_ENABLED = "redunda.enabled";
	private static final String PROP_KEY_REDUNDA_APIKEY = "redunda.apikey";

	private final ProgramProperties props;

	private volatile ChatManager chatManager;

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

		new Thread(() -> {
			// redunda management
			final boolean redundaEnabled = props.getBoolean(PROP_KEY_REDUNDA_ENABLED, false);
			RedundaService redundaService =
					redundaEnabled ? RedundaService.startAndWaitForGo(props.getProperty(PROP_KEY_REDUNDA_APIKEY),
							standby -> {
								if (standby) {
									onStandby();
									chatManager.restart();
								}
							}) : null;

			onStart();

			// start chat loop (blocking)
			chatManager.start(props.getProperty(PROP_KEY_CHAT_USERMAIL),
					props.getProperty(PROP_KEY_CHAT_USERPASS),
					props.getInt(PROP_KEY_CHAT_DEVROOM),
					props.getIntArray(PROP_KEY_CHAT_ROOMS),
					GenericBot.this::onConnected);

			if (redundaService != null) {
				redundaService.stop();
			}

			onShutdown();

			boolean restart = chatManager.isRestarting();

			chatManager = null;

			if (restart) {
				start();
			}
		}, "bot").start();
	}


	/**
	 * Pull the plug.
	 */
	public final void stop() {
		if (chatManager == null) {
			throw new IllegalStateException("GenericBot not running");
		}
		chatManager.terminate();
	}


	/**
	 * Callback to setup everything the bot may need.
	 * Executed before the bot connects to the chat.
	 */
	protected void onStart() {
		// to be overridden
	}


	/**
	 * Bot received a standby signal from Redunda, a restart will be triggered.
	 */
	protected void onStandby() {
		// to be overridden
	}


	/**
	 * Callback when the bot is connected to a chat room.
	 * @param chatroom Room conntected to.
	 */
	protected void onConnected(Room chatroom) {
		// to be overridden
	}


	/**
	 * Callback on termination, after chat is disconnected.
	 */
	protected void onShutdown() {
		// to be overridden
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


	public Logger getLogger() {
		return LOGGER;
	}

}
