package com.floern.genericbot.frame;

import com.floern.genericbot.frame.chat.commands.AliveCommand;
import com.floern.genericbot.frame.chat.commands.BotsAliveCommand;
import com.floern.genericbot.frame.chat.commands.CommandsCommand;
import com.floern.genericbot.frame.chat.commands.RestartCommand;
import com.floern.genericbot.frame.chat.commands.StatusCommand;
import com.floern.genericbot.frame.chat.commands.TerminateCommand;
import com.floern.genericbot.frame.chat.commands.TheTrainCommand;
import com.floern.genericbot.frame.utils.ProgramProperties;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import fr.tunaki.stackoverflow.chat.Room;

public class GenericBotTest {


	@Test
	public void testBot() throws Exception {
		TestBot bot = new TestBot();
		bot.registerCommand(new AliveCommand())
				.registerCommand(new RestartCommand())
				.registerCommand(new TerminateCommand())
				.registerCommand(new CommandsCommand())
				.registerCommand(new BotsAliveCommand())
				.registerCommand(new TheTrainCommand())
				.registerCommand(new StatusCommand()
						.registerStatusRecordCallback(new StatusCommand.UptimeStatusRecordCallback())
						.registerStatusRecordCallback(new StatusCommand.LocationStatusRecordCallback(ProgramProperties.load("data/bot.properties")))
				)
				.start();

		bot.waitForTermination();
	}



	private static class TestBot extends GenericBot {

		private final CountDownLatch hold = new CountDownLatch(1);

		public TestBot() {
			super(ProgramProperties.load("data/bot.properties"));
		}

		@Override
		protected void onConnected(Room room) {
			room.send("hello!");
		}

		@Override
		protected void onShutdown() {
			hold.countDown();
		}

		private void waitForTermination() throws InterruptedException {
			hold.await();
		}

	}


}