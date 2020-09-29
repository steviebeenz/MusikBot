package io.github.steviebeenz.musikbot.core;
import io.github.steviebeenz.musikbot.Main;

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

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class UpdateChecker extends Main {
    private UpdateChecker() {}
    private static UpdateChecker instance = new UpdateChecker();
    public static UpdateChecker getInstance() {
        return instance;
    }

    private String latestVersion;
    private String latestDl;

    private boolean updateAvailable;
    private boolean updating;

    private JavaPlugin plugin = new Main();

    private Gson gson = new Gson();

    /*
     * UTILITIES
     */

    public void checkForUpdate() {
        try {
            //Latest version number.
            String latestVersionInfo = readFrom("https://github.com/steviebeenz/MusikBot/raw/patch-1/update.json");

            Type type = new TypeToken<JsonObject>() {}.getType();
            JsonObject object = gson.fromJson(latestVersionInfo, type);
            latestDl = object.get("dl").getAsString();
            latestVersion = object.get("ver").getAsString();
            updateAvailable = !latestVersion.equals(plugin.getDescription().getVersion());
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void update(Player initiator) {
        checkForUpdate();

        if (updateAvailable && !updating) {
            sendActionBar(initiator, ChatColor.translateAlternateColorCodes('&', "&f&lUPDATING &1&lMusikBot &b&lV" + plugin.getDescription().getVersion() + " &a&l» &b&lV" + latestVersion + " &8[RETREIVING UPDATER]"));

            updating = true;
            boolean delete = true;

            try {
                if (!Bukkit.getPluginManager().isPluginEnabled("AutoUpdaterAPI")) {
                    delete = false;

                    //Download AutoUpdaterAPI
                    URL url = new URL(latestDl);
                    HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                    httpConnection.setRequestProperty("User-Agent", "SpigetResourceUpdater");
                    long completeFileSize = httpConnection.getContentLength();

                    BufferedInputStream in = new BufferedInputStream(httpConnection.getInputStream());
                    FileOutputStream fos = new FileOutputStream(new File(plugin.getDataFolder().getPath().substring(0, plugin.getDataFolder().getPath().lastIndexOf("/")) + "/AutoUpdaterAPI.jar"));
                    BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);

                    byte[] data = new byte[1024];
                    long downloadedFileSize = 0;
                    int x;
                    while ((x = in.read(data, 0, 1024)) >= 0) {
                        downloadedFileSize += x;

                        if (downloadedFileSize % 5000 == 0) {
                            final int currentProgress = (int) ((((double) downloadedFileSize) / ((double) completeFileSize)) * 15);

                            final String currentPercent = String.format("%.2f", (((double) downloadedFileSize) / ((double) completeFileSize)) * 100);

                            String bar = "&a:::::::::::::::";

                            bar = bar.substring(0, currentProgress + 2) + "&c" + bar.substring(currentProgress + 2);

                            sendActionBar(initiator, ChatColor.translateAlternateColorCodes('&', "&f&lUPDATING &1&lMusikBot &b&lV" + plugin.getDescription().getVersion() + " &a&l» &b&lV" + latestVersion + " &8&l| " + bar + " &8&l| &2" + currentPercent + "% &8[DOWNLOADING UPDATER]"));
                        }

                        bout.write(data, 0, x);
                    }

                    bout.close();
                    in.close();

                    sendActionBar(initiator, ChatColor.translateAlternateColorCodes('&', "&f&lUPDATING &1&lMusikBot &b&lV" + plugin.getDescription().getVersion() + " &a&l» &b&lV" + latestVersion + " &8[RUNNING UPDATER]"));

                    Plugin target = Bukkit.getPluginManager().loadPlugin(new File(plugin.getDataFolder().getPath().substring(0, plugin.getDataFolder().getPath().lastIndexOf("/")) + "/AutoUpdaterAPI.jar"));
                    target.onLoad();
                    Bukkit.getPluginManager().enablePlugin(target);
                }

                //TODO Plugin shutdown logic.

                UpdateLocale locale = new UpdateLocale();

                //TODO Message configuration.

                locale.setFileName("MusikBot-" + latestVersion);
                locale.setPluginName("MusikBot");

                new Updater(initiator, plugin, 39719, locale, delete, true).update();
            } catch (Exception ex) {
                ex.printStackTrace();
                sendActionBar(initiator, ChatColor.translateAlternateColorCodes('&', "&f&lUPDATING &1&lMusikBot &b&lV" + plugin.getDescription().getVersion() + " &b&l» &1&lV" + latestVersion + " &8[&c&lUPDATE FAILED &7&o(Check Console)&8]"));
            }
        }
    }


    /*
     * GETTERS
     */

    public String getLatestVersion() {
        return latestVersion;
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    /*
     * PRIVATE UTILITIES
     */

    private String readFrom(String url) throws IOException
    {
        try (InputStream is = new URL(url).openStream())
        {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }

            return sb.toString();
        }
    }

    private void sendActionBar(Player player, String message) {
        if (player != null)
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', message)));
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (player.isOp()) {
            //player is op
            update(player);
        } else if (player.hasPermission("MusikBot.update")) {
            update(player);
        }
    }
}