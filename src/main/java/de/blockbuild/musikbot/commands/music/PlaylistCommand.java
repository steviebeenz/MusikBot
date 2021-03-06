package de.blockbuild.musikbot.commands.music;

import java.io.File;
import java.util.List;

import com.github.breadmoirai.discordemoji.Emoji;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.blockbuild.musikbot.Bot;
import de.blockbuild.musikbot.commands.MusicCommand;
import de.blockbuild.musikbot.configuration.PlaylistConfiguration;
import de.blockbuild.musikbot.core.TrackScheduler;

public class PlaylistCommand extends MusicCommand {
	List<String> tracks;
	private int amount;

	public PlaylistCommand(Bot bot) {
		super(bot);
		this.name = "playlist";
		this.aliases = new String[] { "pl" };
		this.help = "save, load, delete or list your playlists";
		this.arguments = "<save|delete|load|list> <name>";
		this.joinOnCommand = true;
	}

	@Override
	protected void doGuildCommand(CommandEvent event) {
		StringBuilder builder = new StringBuilder().append(event.getClient().getSuccess());
		TrackScheduler trackScheduler = musicManager.getTrackScheduler();

		String x[] = args.split(" ", 2);

		if (args.isEmpty() || x.length < 2) {
			if (args.startsWith("list") || args.startsWith("show") || args.startsWith("load")) {
				builder.append(" **Your Playlists:**\n");
				File[] filelist = new File(bot.getMain().getDataFolder(), "/Playlists/" + user.getId() + "/")
						.listFiles();
				if (filelist.length == 0) {
					builder.append("`No saved playlist.`");
				} else {
					for (File file : filelist) {
						builder.append("`").append(file.getName().substring(0, file.getName().length() - 4))
								.append("`\n");
					}
				}
				event.reply(builder.toString());
			} else {
				sendCommandInfo(event);
			}
			return;
		}

		String name = x[1];
		PlaylistConfiguration playlist = new PlaylistConfiguration(bot, user, name);

		switch (x[0]) {
		case "save":
		case "create":
			if (musicManager.getAudioPlayer().getPlayingTrack() == null) {
				builder.append(" First add some tracks to the player.");
				event.reply(builder.toString());
				return;
			}
			playlist.clearPlaylist();
			playlist.addTrack(musicManager.getAudioPlayer().getPlayingTrack().getInfo().uri);
			playlist.addTracks(trackScheduler.getQueue());

			if (playlist.writeConfig()) {
				builder.append(" Successfully saved playlist **").append(name).append("** containing **")
						.append(playlist.getAmount()).append("** Tracks");
			} else {
				builder.append(" Failed to save playlist **").append(name).append("**");
			}
			event.reply(builder.toString());

			break;
		case "remove":
		case "delete":
		case "del":
			playlist.deleteConfig();
			builder.append(" Successfully deleted: **").append(name).append("**.");
			event.reply(builder.toString());

			break;
		case "load":
			event.reply(Emoji.MAG_RIGHT.getUtf8() + " Loading...", m -> {
				StringBuilder builder1 = new StringBuilder();
				if (playlist.getPlaylist().isEmpty()) {
					builder1.append(event.getClient().getWarning());
					builder1.append(" Playlist `").append(name).append("` dosen't exsist.");
				} else {
					amount = 0;
					for (String track : playlist.getPlaylist()) {
						bot.getPlayerManager().loadItemOrdered(musicManager, track,
								new ResultHandler(trackScheduler, event));
					}

					builder1.append(event.getClient().getSuccess());
					builder1.append(" Successfully load **").append(amount);
					builder1.append("** tracks from Playlist **").append(name).append("**");
				}
				m.editMessage(builder1.toString()).queue();
			});
			break;
		case "list":
		case "show":
			builder.append(" Playlist **").append(name).append(":**\n");
			for (String track : playlist.getPlaylist()) {
				builder.append("`").append(track).append("`\n");
			}
			event.reply(builder.toString());

			break;
		default:
			sendCommandInfo(event);
			break;
		}
	}

	@Override
	protected void doPrivateCommand(CommandEvent event) {
		event.reply(event.getClient().getError() + " This command cannot be used in Direct messages.");

		StringBuilder builder = new StringBuilder().append(event.getClient().getSuccess());

		builder.append(" **MusikBot** ").append("by Block-Build\n");
		builder.append("SpigotMC: `https://www.spigotmc.org/resources/the-discord-musikbot-on-minecraft.64277/`\n");
		builder.append("GitHub: `https://github.com/Block-Build/MusikBot`\n");
		builder.append("Version: `").append(bot.getMain().getDescription().getVersion()).append("`\n");
		builder.append("Do you have any problem or suggestion? Open an Issue on GitHub.");

		event.reply(builder.toString());
	}

	private class ResultHandler implements AudioLoadResultHandler {
		private TrackScheduler trackScheduler;
		private CommandEvent event;

		public ResultHandler(TrackScheduler trackScheduler, CommandEvent event) {
			this.trackScheduler = trackScheduler;
			this.event = event;
		}

		@Override
		public void trackLoaded(AudioTrack track) {
			trackScheduler.queueTrack(track);
			amount++;
		}

		@Override
		public void playlistLoaded(AudioPlaylist playlist) {
			// should never called
		}

		@Override
		public void noMatches() {
			StringBuilder builder = new StringBuilder(event.getClient().getError());
			builder.append(" No result found: **").append(args).append("**");
			event.reply(builder.toString());
		}

		@Override
		public void loadFailed(FriendlyException throwable) {
			StringBuilder builder = new StringBuilder(event.getClient().getError());
			builder.append(" Faild to load **").append(args).append("**");
			event.reply(builder.toString());
		}
	}
}
