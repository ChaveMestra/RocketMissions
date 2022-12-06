package com.rocketmc.quests.missions;

import com.connorlinfoot.titleapi.TitleAPI;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.rocketmc.quests.utils.ChatUtils.f;

@Getter
@Setter
@AllArgsConstructor
public class Mission {
    private Rarity rarity;
    private Objective objective;
    private int goal;
    private List<String> rewards;
    private List<String> commands;

    public void finishMission(Player player) {
        for (String cmd : this.commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%jogador%", player.getName()));
        }
        TitleAPI.sendTitle(player, 30, 80, 30, f("&aCRIME COMETIDO"), f("&7Voce foi um fora da lei e recebeu uma recompensa!"));
        player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 1, 1);
    }

    public ItemStack toItemStack() {
        ItemStack missionItem = new ItemStack(Material.PAPER);

        //add unique UUID para nao stackar
        net.minecraft.server.v1_8_R3.ItemStack stack = CraftItemStack.asNMSCopy(missionItem);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("UUID", UUID.randomUUID().toString());
        stack.setTag(tag);
        missionItem = CraftItemStack.asBukkitCopy(stack);
        //fim do nbt

        ItemMeta meta = missionItem.getItemMeta();
        meta.setDisplayName(f(this.rarity.getColor() + "Crime"));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(f("&e&l* &fObjetivo:"));
        lore.add(f("&7" + this.objective.getDescricao()));
        lore.add(f("&e&l* &fProgresso:"));
        lore.add(f(this.rarity.getColor() + "0&7/" + this.rarity.getColor() + this.goal));
        lore.add(f("&e&l* &fRecompensas:"));
        for (String recompensa : this.rewards) {
            lore.add(f("&8- " + recompensa));
        }
        lore.add(" ");
        lore.add(f("&c&lCRIMES APENAS DE NOITE"));
        lore.add(f(this.rarity.getDescricao()));
        meta.setLore(lore);
        missionItem.setItemMeta(meta);
        return missionItem;
    }
}
