package com.uniocraft.anticrash.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Bukkitt extends JavaPlugin implements Listener {

    public static char BAD_CHARACTER = '\u0307';

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    public static boolean containsBadCharacter(String string) {
        return string.contains(String.valueOf(BAD_CHARACTER));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (containsBadCharacter(event.getMessage())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("cancelMessage")));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player oyuncu = event.getPlayer();
        if (containsBadCharacter(event.getMessage())) {
            event.setCancelled(true);
            oyuncu.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("cancelMessage")));
        }
    }

    private boolean checkItem(ItemStack item) {
        if (item == null) return false;
        String itemName = item.getItemMeta().getDisplayName();
        List<String> lore = item.getItemMeta().getLore();

        if (itemName != null && containsBadCharacter(itemName)) return true;

        if (lore == null) return false;

        for (String line : lore) {
            if (line != null && containsBadCharacter(line)) return true;
        }
        return false;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInHand();

        if (checkItem(item)) {
            player.getInventory().setItemInHand(null);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("itemsDeleted")));
        }

        try {
            ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
            if (checkItem(itemInOffHand)) {
                player.getInventory().setItemInOffHand(null);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("itemsDeleted")));
            }
        } catch (Exception ignored) {}
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof AnvilInventory)) return;

        HumanEntity player = event.getWhoClicked();
        InventoryView view = event.getView();
        int rawSlot = event.getRawSlot();

        if (rawSlot == view.convertSlot(rawSlot) && rawSlot == 2) { // To be honest I have no idea why I'm checking item like that. But this plugin is so old I won't change something that I don't understand how it works.
            ItemStack item = event.getCurrentItem();
            if (checkItem(item)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("cancelMessage")));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSignChange(SignChangeEvent event) {
        for (String line : event.getLines()) {
            if (line.matches("^[a-zA-Z0-9_]*$") && line.length() > 16) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("cancelMessage")));
            }
        }
    }
}
