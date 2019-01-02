package de.blockbuild.musikbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;

import de.blockbuild.musikbot.Main;
import de.blockbuild.musikbot.core.GuildMusicManager;
import de.blockbuild.musikbot.core.MBCommand;

public class ConfigCommand extends MBCommand {

	public ConfigCommand(Main main) {
		super(main);
		this.name = "config";
		this.aliases = new String[] { "cfg" };
		this.help = "To load or save the Config";
		this.arguments = "<save|load>";
		this.joinOnCommand = false;
		this.category = SETUP;
	}

	@Override
	protected void doCommand(CommandEvent event) {
		GuildMusicManager musicManager = main.getBot().getGuildAudioPlayer(event.getGuild());

		StringBuilder builder = new StringBuilder();
		if (event.getArgs().startsWith("save")) {
			if (musicManager.config.writeConfig()) {
				builder.append(event.getClient().getSuccess()).append(" Config saved successfully");
			} else {
				builder.append(event.getClient().getError()).append(" Faild to save config");
			}
		} else if (event.getArgs().startsWith("load")) {
			if (musicManager.config.loadConfig()) {
				builder.append(event.getClient().getSuccess()).append(" Config load successfully");
			} else {
				builder.append(event.getClient().getError()).append(" Faild to load config");
			}
		} else {
			sendCommandInfo(event);
		}
		event.reply(builder.toString());
	}
}