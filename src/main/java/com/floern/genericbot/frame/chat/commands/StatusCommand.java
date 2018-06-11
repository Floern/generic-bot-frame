/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.chat.commands;

import com.floern.genericbot.frame.chat.ChatManager;
import com.floern.genericbot.frame.chat.commands.categories.MetaCommandCategory;
import com.floern.genericbot.frame.chat.commands.classes.Command;
import com.floern.genericbot.frame.utils.MapUtil;
import com.floern.genericbot.frame.utils.ProgramProperties;
import com.floern.genericbot.frame.utils.StringUtil;
import com.google.common.base.Strings;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import fr.tunaki.stackoverflow.chat.Message;
import fr.tunaki.stackoverflow.chat.Room;

public class StatusCommand extends Command implements MetaCommandCategory {


	private final Collection<StatusRecordCallback> statusRecordCallbacks = new LinkedList<>();


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
		final StringBuilder sb = new StringBuilder();

		final String linePrefix = "    ";
		final int labelWidth = 18;

		statusRecordCallbacks.forEach(statusRecordCallback -> statusRecordCallback.getStatusLine().forEach((label, value) ->
				sb.append(linePrefix).append(StringUtil.padRight(label, labelWidth)).append(' ').append(value).append('\n')));

		return sb.toString();
	}


	@Override
	public ResponseType getResponseType() {
		return ResponseType.NORMAL;
	}


	/**
	 * Register a status record supplier for this Status command.
	 * @param statusRecordCallback
	 * @return this Status command instance for chaining.
	 */
	public StatusCommand registerStatusRecordCallback(StatusRecordCallback statusRecordCallback) {
		statusRecordCallbacks.add(statusRecordCallback);
		return this;
	}


	/**
	 * Status record callback to provide status information for the status command.
	 */
	public interface StatusRecordCallback {
		/**
		 * Get one or multiple status records.
		 * @return map: label -> value
		 */
		Map<String, String> getStatusLine();
	}


	/**
	 * Status record for the bot's uptime.
	 */
	public static class UptimeStatusRecordCallback implements StatusRecordCallback {
		private long startTime = System.currentTimeMillis();

		@Override
		public Map<String, String> getStatusLine() {
			long diff = System.currentTimeMillis() - startTime;

			StringBuilder uptime =  new StringBuilder();
			long secs = diff / 1000 % 60;
			long mins = diff / (60 * 1000) % 60;
			long hours = diff / (60 * 60 * 1000) % 24;
			long days = diff / (24 * 60 * 60 * 1000);
			if (days >= 1) uptime.append(days).append("d ");
			if (hours >= 1 || days >= 1) uptime.append(hours).append("h ");
			if (mins >= 1 || hours >= 1 || days >= 1) uptime.append(mins).append("m ");
			if (secs >= 1 || mins >= 1 || hours >= 1 || days >= 1) uptime.append(secs).append("s");

			Map<String, String> entries = new LinkedHashMap<>();
			entries.put("uptime", uptime.toString());
			entries.put("running since", new Date(startTime).toString());
			return entries;
		}
	}


	/**
	 * Status record for the bot's location.
	 */
	public static class LocationStatusRecordCallback implements StatusRecordCallback {
		private ProgramProperties programProperties;

		public LocationStatusRecordCallback(ProgramProperties programProperties) {
			this.programProperties = programProperties;
		}

		@Override
		public Map<String, String> getStatusLine() {
			String location = programProperties.getProperty("info.location");
			if (Strings.isNullOrEmpty(location)) {
				try {
					location = InetAddress.getLocalHost().getHostName();
				}
				catch (UnknownHostException e) {
					location = "unknown";
				}
			}
			return MapUtil.createSingle("location", location);
		}
	}

}
