package com.floern.genericbot.frame;

import com.floern.genericbot.frame.chat.commands.AliveCommand;
import com.floern.genericbot.frame.chat.commands.BotsAliveCommand;
import com.floern.genericbot.frame.chat.commands.CommandsCommand;
import com.floern.genericbot.frame.chat.commands.RestartCommand;
import com.floern.genericbot.frame.chat.commands.TerminateCommand;
import com.floern.genericbot.frame.chat.commands.TheTrainCommand;
import com.floern.genericbot.frame.utils.ProgramProperties;

import org.junit.Test;

public class GenericBotTest {


	@Test
	public void minimalBot() throws Exception {

		new MinimalBot()
				.registerCommand(new AliveCommand())
				.registerCommand(new RestartCommand())
				.registerCommand(new TerminateCommand())
				.registerCommand(new CommandsCommand())
				.registerCommand(new BotsAliveCommand())
				.registerCommand(new TheTrainCommand())
				.start();

	}



	private static class MinimalBot extends GenericBot {

		public MinimalBot() {
			super(ProgramProperties.load(""));
		}

		@Override
		protected void onLine() {
			getChatManager().getDevChatRoom().send("hello!");
		}

	}


}