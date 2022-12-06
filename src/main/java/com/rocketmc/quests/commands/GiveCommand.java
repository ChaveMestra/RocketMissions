package com.rocketmc.quests.commands;

import com.rocketmc.quests.Main;
import com.rocketmc.quests.missions.Mission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) return false;
        if (Bukkit.getPlayer(args[0]) == null) return false;
        String raridade = args[1];
        String tipo = args[2];
        Mission mission = Main.getInstance().getMissionsManager().getMission(raridade);
        Bukkit.getPlayer(args[0]).getInventory().addItem(mission.toItemStack());
        return false;
    }
}
