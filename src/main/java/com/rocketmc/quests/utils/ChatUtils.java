package com.rocketmc.quests.utils;

import org.bukkit.ChatColor;

public class ChatUtils {

    public static String f(String format) {
        return ChatColor.translateAlternateColorCodes('&', format);
    }
}
