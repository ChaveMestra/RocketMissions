package com.rocketmc.quests.inventory;

import com.rocketmc.quests.Main;
import com.rocketmc.quests.missions.Mission;
import com.rocketmc.quests.missions.Objective;
import com.rocketmc.quests.missions.Rarity;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

import static com.rocketmc.quests.utils.ChatUtils.f;

public class InventoryUtils {

    public static HashMap<ItemStack, Mission> getMissionsFromInventory(Player player) {
        HashMap<ItemStack, Mission> missoes = new HashMap<>();
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null) continue;
            if (!itemStack.hasItemMeta()) continue;
            if (!itemStack.getItemMeta().hasDisplayName()) continue;
            if (!itemStack.getItemMeta().hasLore()) continue;
            if (!ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).contains("Crime")) continue;
            Rarity rarity = null;
            Objective objective = null;
            int goal = 0;
            for (String lore : itemStack.getItemMeta().getLore()) {
                String loresemcor = ChatColor.stripColor(lore);
                if (loresemcor.contains("/") && !loresemcor.contains("/c")) {
                    goal = Integer.parseInt(loresemcor.split("/")[1]);
                }
                if (Rarity.getFromLore(lore) != null) {
                    rarity = Rarity.getFromLore(lore);
                }
                if (Objective.getFromLore(loresemcor) != null) {
                    objective = Objective.getFromLore(loresemcor);
                }
            }
            if (rarity == null || objective == null || goal == 0) continue;
            for (Mission mission : Main.getInstance().getMissionsManager().getLoadedMissions()) {
                if (mission.getGoal() == goal && mission.getObjective() == objective && mission.getRarity() == rarity) {
                    missoes.put(itemStack, mission);
                }
            }
        }

        return missoes;
    }

    public static boolean updateMissionItemStack(ItemStack itemStack, Mission mission, int quantity) {
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = meta.getLore();
        boolean complete = false;
        for (int i = 0; i < lore.size(); i++) {
            String loresemcor = ChatColor.stripColor(lore.get(i));
            if (loresemcor.contains("/")) {
                int atual = Integer.parseInt(loresemcor.split("/")[0]);
                int newAtual = atual + quantity;
                if (newAtual >= mission.getGoal()) {
                    newAtual = mission.getGoal();
                    complete = true;
                }
                lore.set(i, f(mission.getRarity().getColor() + newAtual + "&7/" + mission.getRarity().getColor() + mission.getGoal()));

            }
        }

        if (complete && !lore.get(lore.size() - 1).contains("COMETIDO")) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            lore.add(f("&a&lCRIME COMETIDO &8(&7Botao Direito&8)"));
        } else if (complete) {
            return false;
        }

        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return true;

    }

    public static boolean isMissionCompleteFromItemstack(ItemStack itemStack) {
        List<String> lore = itemStack.getItemMeta().getLore();
        return lore.get(lore.size() - 1).contains("COMETIDO");
    }

}
