/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.chat.commands;

import com.floern.genericbot.frame.chat.ChatManager;
import com.floern.genericbot.frame.chat.commands.classes.Command;
import com.floern.genericbot.frame.chat.commands.classes.CommandCategory;
import com.floern.genericbot.frame.chat.commands.classes.MetaCommandCategory;
import com.floern.genericbot.frame.utils.StringUtil;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.tunaki.stackoverflow.chat.Message;
import fr.tunaki.stackoverflow.chat.Room;

public class CommandsCommand extends Command implements MetaCommandCategory {


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
		final int labelWidth = showAll ? 30 : 16;
		final int maxAliases = 4;

		List<Command> commands = chatManager.getAllCommands().stream()
				.filter(command -> command.getUsageDescription() != null
					&& (showAll || command.isCommanderPrivileged(chatManager, message.getUser())))
				.collect(Collectors.toList());

		// create command groups
		Map<String, List<Command>> commandGroups = new LinkedHashMap<>();
		commands.stream().map(CommandCategory::getCommandCategory).distinct()
				.forEach(category -> commandGroups.put(category, new LinkedList<>()));

		// allot commands to their categories
		commands.forEach(command -> commandGroups.get(command.getCommandCategory()).add(command));

		// print commands
		commandGroups.forEach((categoryName, commandsGroup) -> {
			sb.append(categoryName).append("\n");
			commandsGroup.forEach(command -> {
				sb.append(StringUtil.padRight(showAll
								? String.join("|", Arrays.asList(command.getAliases())
									.subList(0, Math.min(maxAliases, command.getAliases().length)))
								: command.getAliases()[0], labelWidth))
						.append(" - ")
						.append(command.getUsageDescription())
						.append('\n');
			});
		});

		return sb.toString();
	}

}
