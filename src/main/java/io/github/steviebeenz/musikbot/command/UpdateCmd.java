package io.github.steviebeenz.musikbot.core;
import io.github.steviebeenz.musikbot.Main;
import io.github.steviebeenz.musikbot.core.UpdateChecker;

import com.gamerking195.dev.autoupdaterapi.UpdateLocale;
import com.gamerking195.dev.autoupdaterapi.Updater;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class UpdateCmd implements CommandExecutor {


@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            //UpdateChecker.update(player);
        }

        // If the player (or console) uses our command correct, we can return true
        return true;
    }
}