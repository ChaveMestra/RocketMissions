package com.rocketmc.quests.placeholders;

import com.rocketmc.quests.Main;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CrimePlaceholders extends EZPlaceholderHook {


    public CrimePlaceholders(Plugin plugin, String identifier) {
        super(plugin, identifier);
    }

    @Override
    public String onPlaceholderRequest(Player p, String arg) {


        if (p == null) return "";

        if (arg.equals("cooldown")) {

            if (Main.getInstance().getCrimeManager().isCommitingCrime(p.getName())) {
                return (Main.getInstance().getCrimeManager().getCommitingCrime().get(p.getName()) - System.currentTimeMillis() / 1000) + "s";
            }
        }

        return "";
    }
}
