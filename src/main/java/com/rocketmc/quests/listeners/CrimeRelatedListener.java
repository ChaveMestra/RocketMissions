package com.rocketmc.quests.listeners;

import com.rocketmc.quests.Main;
import com.sk89q.worldguard.protection.events.DisallowedPVPEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import static com.rocketmc.quests.utils.ChatUtils.f;

public class CrimeRelatedListener implements Listener {


    @EventHandler
    public void onDisallow(DisallowedPVPEvent e) {
        if (Main.getInstance().getCrimeManager().isCommitingCrime(e.getDefender().getName())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onHit(EntityDamageByEntityEvent e) {

        if (e.getDamager() instanceof Player) {
            Player attacker = (Player) e.getDamager();
            if (e.getEntity() instanceof Player) {
                Player vitima = (Player) e.getEntity();
                if (Main.getInstance().getCrimeManager().isCommitingCrime(vitima.getName())) {
                    if (e.isCancelled()) {
                        vitima.damage(e.getDamage(), attacker);
                    }
                }
            }
        }

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (Main.getInstance().getCrimeManager().isCommitingCrime(e.getEntity().getName())) {
            Main.getInstance().getCrimeManager().resetCrimeCooldown(e.getEntity().getName(), true);
        }
    }


    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (Main.getInstance().getCrimeManager().isCommitingCrime(e.getPlayer().getName())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(f("&cVoce é um fora da lei e por isso tem comandos bloqueados"));
        }
    }
}
