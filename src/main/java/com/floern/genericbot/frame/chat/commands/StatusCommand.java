/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.chat.commands;

import com.floern.genericbot.frame.chat.ChatManager;
import com.floern.genericbot.frame.chat.commands.classes.Command;
import com.floern.genericbot.frame.chat.commands.classes.MetaCommandCategory;
import com.floern.genericbot.frame.utils.ProgramProperties;
import com.floern.genericbot.frame.utils.StringUtil;
import com.google.common.base.Strings;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

import fr.tunaki.stackoverflow.chat.Message;
import fr.tunaki.stackoverflow.chat.Room;

public class StatusCommand extends Command implements MetaCommandCategory {

	private long startTime = System.currentTimeMillis();

	private ProgramProperties programProperties;

	private final Collection<StatusRecordCallback> statusRecordCallbacks = new HashSet<>();


	public StatusCommand(ProgramProperties programProperties) {
		this.programProperties = programProperties;
	}


	@Override
	public String[] getAliases() {
		return new String[] { "status" };
	}


	@Override
	public String getUsageDescription() {
		return "show status info";
	}


	@Override
	protected String execute(ChatManager chatManager, Room chatroom, Message message, String[] args) {
		String location = programProperties.getProperty("info.location");
		if (Strings.isNullOrEmpty(location)) {
			try {
				location = InetAddress.getLocalHost().getHostName();
			}
			catch (UnknownHostException e) {
				location = "unknown";
			}
		}

		StringBuilder sb = new StringBuilder();

		String linePrefix = "    ";
		int labelWidth = 18;

		sb.append(linePrefix).append(StringUtil.padRight("running since", labelWidth)).append(' ')
				.append(new Date(startTime).toString()).append('\n');
		sb.append(linePrefix).append(StringUtil.padRight("location", labelWidth)).append(' ')
				.append(location).append('\n');

		statusRecordCallbacks.forEach(statusRecordCallback -> statusRecordCallback.getStatusLine().forEach((label, value) ->
				sb.append(linePrefix).append(StringUtil.padRight(label, labelWidth)).append(' ').append(value).append('\n')));

		return sb.toString();
	}


	public void registerStatusRecordCallback(StatusRecordCallback statusRecordCallback) {
		statusRecordCallbacks.add(statusRecordCallback);
	}


	public interface StatusRecordCallback {
		Map<String, String> getStatusLine();
	}

}
