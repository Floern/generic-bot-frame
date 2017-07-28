/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.chat.commands;

import com.floern.genericbot.frame.chat.ChatManager;
import com.floern.genericbot.frame.chat.commands.classes.Command;
import com.floern.genericbot.frame.utils.StringUtil;

import java.util.Arrays;
import java.util.List;

import fr.tunaki.stackoverflow.chat.Message;
import fr.tunaki.stackoverflow.chat.Room;

public class CommandsCommand extends Command {


	private final List<Command> availableCommands;


	public CommandsCommand(List<Command> availableCommands) {
		this.availableCommands = availableCommands;
	}


	@Override
	public String[] getAliases() {
		return new String[] { "commands", "cmds" };
	}


	@Override
	public String getUsageDescription() {
		return "show available commands";
	}


	@Override
	protected String execute(ChatManager chatManager, Room chatroom, Message message, String[] args) {
		StringBuilder sb = new StringBuilder();

		boolean showAll = args.length >= 2 && ("all".equals(args[1]) || "full".equals(args[1]));

		final String[] commandCategory = { "" };
		availableCommands.stream().filter(command -> command.getUsageDescription() != null
				&& (showAll || command.isCommanderPrivileged(chatManager, message.getUser())))
				.forEach(command -> sb
						.append(!commandCategory[0].equals(commandCategory[0] = command.getCommandCategory())
								? ("    " + commandCategory[0] + "\n") : "")
						.append("       ")
						.append(StringUtil.padRight(showAll
										 ? String.join("|", Arrays.asList(command.getAliases())
												.subList(0, Math.min(4, command.getAliases().length)))
										 : command.getAliases()[0],
								showAll ? 30 : 16))
						.append(" - ")
						.append(command.getUsageDescription())
						.append('\n'));

		return sb.toString();
	}

}
