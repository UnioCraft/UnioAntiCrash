package com.uniocraft.anticrash.bungee;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.*;

public class Bungee extends Plugin implements Listener {

    private Configuration config;

    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, this);

        try {
            saveDefaultConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDefaultConfig() throws IOException {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                try (InputStream is = getResourceAsStream("config.yml");
                     OutputStream os = new FileOutputStream(configFile)) {
                    ByteStreams.copy(is, os);
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to create configuration file", e);
            }
        }
        config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
    }

    public static char BAD_CHARACTER = '\u0307';

    public static boolean containsBadCharacter(String string) {
        return string.contains(String.valueOf(BAD_CHARACTER));
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (event.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) event.getSender();
            if (containsBadCharacter(event.getMessage())) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("cancelMessage")));
            }
        }
    }
}