package com.rocketmc.quests.listeners;

import com.rocketmc.quests.inventory.InventoryUtils;
import com.rocketmc.quests.missions.Mission;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class CompleteListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        ItemStack itemStack = e.getPlayer().getItemInHand();
        if (itemStack == null) return;
        if (!itemStack.hasItemMeta()) return;
        if (!itemStack.getItemMeta().hasDisplayName()) return;
        if (!itemStack.getItemMeta().hasLore()) return;
        if (!ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).contains("Crime")) return;
        for (Map.Entry<ItemStack, Mission> entry : InventoryUtils.getMissionsFromInventory(e.getPlayer()).entrySet()) {
            if (entry.getKey().equals(itemStack)) {
                if (InventoryUtils.isMissionCompleteFromItemstack(itemStack)) {
                    e.getPlayer().getInventory().remove(itemStack);
                    entry.getValue().finishMission(e.getPlayer());
                }
            }
        }
    }
}
