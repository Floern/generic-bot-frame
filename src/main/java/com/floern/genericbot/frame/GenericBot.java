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
import org.sobotics.chatexchange.chat.Room;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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
	private RedundaService redundaService;

	private List<Command> addedCommands = new ArrayList<>();

	private final CountDownLatch terminationLatch = new CountDownLatch(1);


	public GenericBot(ProgramProperties props) {
		this.props = props;
	}


	/**
	 * Add a command to the bot.
	 * @param command
	 * @return This GeneriBot instance.
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
	 * @return This GeneriBot instance.
	 */
	public final GenericBot start() {
		if (chatManager != null) {
			throw new IllegalStateException("GenericBot already running");
		}

		chatManager = new ChatManager(props);
		addedCommands.forEach(chatManager::addCommand);

		// start bot core thread
		new Thread(() -> {
			// redunda management
			final boolean redundaEnabled = props.getBoolean(PROP_KEY_REDUNDA_ENABLED, false);
			redundaService = redundaEnabled ? RedundaService.startAndWaitForGo(props.getProperty(PROP_KEY_REDUNDA_APIKEY),
					getAppVersion(),
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
					GenericBot.this::onConnected,
					GenericBot.this::onAllConnected);

			if (redundaService != null) {
				redundaService.stop();
			}

			onShutdown();

			boolean restart = chatManager.isRestarting();

			chatManager = null;

			if (restart) {
				triggerRestart();
			}

			terminationLatch.countDown();
		}, "bot").start();

		return this;
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
	 * Terminate and restart the process.
	 */
	private void triggerRestart() {
		try {
			String jarFile = getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			String cmd = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java -jar " + jarFile;

			LOGGER.info("restart: " + cmd);
			Runtime.getRuntime().exec(cmd);
		}
		catch (IOException | URISyntaxException e) {
			LOGGER.error("terminate and restart", e);
			e.printStackTrace();
		}
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
	 * Callback when the bot is connected to all chat rooms.
	 */
	protected void onAllConnected() {
		// to be overridden
	}


	/**
	 * Callback on termination or restart, after chat is disconnected.
	 */
	protected void onShutdown() {
		// to be overridden
	}


	/**
	 * Wait until the bot terminates.
	 */
	public void waitForTermination() throws InterruptedException {
		terminationLatch.await();
	}


	/**
	 * Get the chat manager instance.
	 */
	public ChatManager getChatManager() {
		return chatManager;
	}


	/**
	 * Get the bot's properties.
	 */
	public ProgramProperties getProgramProperties() {
		return props;
	}


	/**
	 * Get the Redunda service instance.
	 */
	public RedundaService getRedundaService() {
		return redundaService;
	}


	/**
	 * Get the application version name. May be overridden, "unknown" by default.
	 */
	public String getAppVersion() {
		return "unknown";
	}


	public Logger getLogger() {
		return LOGGER;
	}

}
