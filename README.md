# Generic Bot Frame

Generic Bot framework for the Stack Exchange chat written in Java.

## Dependencies

### build.gradle

```java
repositories {
    mavenCentral()
    maven {
        url 'https://raw.githubusercontent.com/Floern/generic-bot-frame/repo/'
    }
    maven {
        url 'https://raw.githubusercontent.com/Tunaki/chatexchange/mvn-repo/'
    }
}

dependencies {
    compile 'com.floern:genericbot-frame:0.9.8'
}
```

## Example Chat Bot

### Main class

```java
public class Main {

	public static void main(String[] rawrgs) throws InterruptedException {
		new ExampleBot().start().waitForTermination();
		System.exit(0);
	}

}
```

### ExampleBot

```java
public class ExampleBot extends GenericBot {

	// comments
	private CommentsLoaderService commentsLoader;

	public GenericBot() {
		super(ProgramProperties.load("data/bot.properties"));

		StatusCommand statusCommand = new StatusCommand();
		statusCommand
				.registerStatusRecordCallback(new StatusCommand.UptimeStatusRecordCallback())
				.registerStatusRecordCallback(new StatusCommand.LocationStatusRecordCallback(getProgramProperties()));

		registerCommand(new AliveCommand());
		registerCommand(new CommandsCommand());
		registerCommand(new TerminateCommand());
		registerCommand(new TheTrainCommand());
		registerCommand(statusCommand);

		// setup service to load new comments from Stack Overflow
		commentsLoader = new CommentsLoaderService(this);
		// provide API quota information to the status command
		statusCommand.registerStatusRecordCallback(() ->
				MapUtil.createSingle("api quota", ApiLoader.getQuotaRemaining() + " (max " + ApiLoader.getQuotaMax() + ")"));

		// setup comment listener
		commentsLoader.addOnCommentLoadedListener(new CommentsLoaderService.OnCommentLoadedListener() {
			public void onCommentLoaded(Comment comment) {
				getChatManager().getDevChatRoom()
						.send("https://stackoverflow.com/posts/comments/" + comment.getCommentId());
			}
		});
	}

	@Override
	protected void onAllConnected() {
		// send a message
		getChatManager().getDevChatRoom().send("hello world!");

		// start the comment service
		ScheduledTaskExecutor.scheduleTask(commentsLoader::start, 0);
	}

	@Override
	protected void onShutdown() {
		// stop the comment service
		commentsLoader.stop();
		ScheduledTaskExecutor.cancelAll();
	}

}
```
