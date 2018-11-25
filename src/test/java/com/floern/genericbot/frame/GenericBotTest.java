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

import org.sobotics.chatexchange.chat.Room;

public class GenericBotTest {


	@Test
	public void testBot() throws Exception {
		new TestBot()
				.registerCommand(new AliveCommand())
				.registerCommand(new RestartCommand())
				.registerCommand(new TerminateCommand())
				.registerCommand(new CommandsCommand())
				.registerCommand(new BotsAliveCommand())
				.registerCommand(new TheTrainCommand())
				.registerCommand(new StatusCommand()
						.registerStatusRecordCallback(new StatusCommand.UptimeStatusRecordCallback())
						.registerStatusRecordCallback(new StatusCommand.LocationStatusRecordCallback(ProgramProperties.load("data/bot.instance.properties")))
				)
				.start()
				.waitForTermination();
	}



	private static class TestBot extends GenericBot {

		public TestBot() {
			super(ProgramProperties.load("data/bot.global.properties", "data/bot.instance.properties"));
		}

		@Override
		protected void onConnected(Room room) {
			room.send("hello!");
		}

	}


}