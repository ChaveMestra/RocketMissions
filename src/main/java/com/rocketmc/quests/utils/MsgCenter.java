/*
 *
 * *
 *  * Created by: Gabriel Batistella & Pedro Aurelio Breda
 *  * Creation Date / Time: 10/12/18 16:46
 *  * Project: RocketNetwork
 *  * Copyright (c) Gabriel Batistella & Pedro Aurelio Breda 2018
 *
 *
 */

//
// Decompiled by Procyon v0.5.30
// 

package com.rocketmc.quests.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MsgCenter {
    private final static int CENTER_PX = 154;
    private final static int MAX_PX = 250;


    public static void broadcastMessage(String message) {
        Bukkit.getOnlinePlayers().forEach(jogador ->
                sendCenteredMessage(jogador, message));
    }

    public static void sendCenteredMessage(Player player, String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;
        int charIndex = 0;
        int lastSpaceIndex = 0;
        String toSendAfter = null;
        String recentColorCode = "";
        for (final char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
                continue;
            } else if (previousCode == true) {
                previousCode = false;
                recentColorCode = "§" + c;
                if (c == 'l' || c == 'L') {
                    isBold = true;
                    continue;
                } else isBold = false;
            } else if (c == ' ') lastSpaceIndex = charIndex;
            else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
            if (messagePxSize >= MAX_PX) {
                toSendAfter = recentColorCode + message.substring(lastSpaceIndex + 1);
                message = message.substring(0, lastSpaceIndex + 1);
                break;
            }
            charIndex++;
        }
        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        player.sendMessage(sb.toString() + message);
        if (toSendAfter != null) sendCenteredMessage(player, toSendAfter);
    }
}