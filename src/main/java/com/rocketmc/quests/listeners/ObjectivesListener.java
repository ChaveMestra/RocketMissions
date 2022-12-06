package com.rocketmc.quests.listeners;

import com.intellectualcrafters.plot.object.Plot;
import com.rocketmc.quests.Main;
import com.rocketmc.quests.missions.Objective;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import rocketmc.skyblock.events.KillPVEEvent;

import java.util.Map;
import java.util.UUID;

import static com.rocketmc.quests.crimes.CrimeManager.day;
import static com.rocketmc.quests.utils.ChatUtils.f;

public class ObjectivesListener implements Listener {


    @EventHandler
    public void onKillPlayer(PlayerDeathEvent e) {
        if (day()) return;


        if (e.getEntity().getKiller() instanceof Player) {
            Player killer = e.getEntity().getKiller();
            if (!Main.getInstance().getMissionsManager().playerHaveObjective(killer, Objective.KILL_PLAYER)) return;
            Main.getInstance().getCrimeManager().addCrime(killer.getName(), null, Objective.KILL_PLAYER);
            Main.getInstance().getMissionsManager().updatePlayerObjective(killer, Objective.KILL_PLAYER, 1);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        boolean commited = false;
        if (day()) return;


        String owner = null;
        if (e.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase("terrenos")) {
            if (!Main.getInstance().getMissionsManager().playerHaveObjective(e.getPlayer(), Objective.FARM_COLLECT))
                return;
            //eh em plots
            Block block = e.getBlock();
            if ((block.getType().toString().equals("NETHER_WARTS") && block.getData() == 3)
                    || (block.getType() == Material.CROPS && block.getData() == 7)
                    || (block.getTypeId() == 17)
                    || (block.getType() == Material.MELON_BLOCK)
                    || (block.getType() == Material.SUGAR_CANE_BLOCK)) {
                Plot plot = Main.getInstance().getPlotAPI().getPlot(e.getBlock().getLocation());
                if (plot == null) return;
                if (plot.getOwners().contains(e.getPlayer().getUniqueId())) return;
                boolean alguemOn = false;
                for (UUID uuid : plot.getOwners()) {
                    if (Bukkit.getPlayer(uuid) != null) {
                        alguemOn = true;
                        owner = Bukkit.getPlayer(uuid).getName();
                    }
                }
                if (!alguemOn) {
                    e.getPlayer().sendMessage(f("&cNao ha ninguem online deste terreno para reagir ao seu crime."));
                    return;
                }

                //ok, tem gente pra defender
                for (UUID uuid : plot.getOwners()) {
                    if (Bukkit.getPlayer(uuid) != null) {
                        Bukkit.getPlayer(uuid).sendMessage(f("&4&lALGUEM ESTA COMETENDO UM CRIME NO SEU PLOT!"));
                    }
                }
                commited = true;
                e.getBlock().setType(Material.AIR);
                Main.getInstance().getCrimeManager().addCrime(e.getPlayer().getName(), owner, Objective.FARM_COLLECT);
                Main.getInstance().getMissionsManager().updatePlayerObjective(e.getPlayer(), Objective.FARM_COLLECT, 1);
            }


        } else {
            if (e.getBlock().getType() == Material.OBSIDIAN && e.getBlock().getLocation().getWorld().getName().equals("world")) {
                if (!Main.getInstance().getMissionsManager().playerHaveObjective(e.getPlayer(), Objective.BLOCK_BREAK))
                    return;
                ApplicableRegionSet set = WGBukkit.getPlugin().getRegionManager(e.getPlayer().getWorld())
                        .getApplicableRegions(e.getBlock().getLocation());
                String mina = "";
                int priority = 0;
                for (ProtectedRegion rg : set.getRegions()) {
                    if (rg.getPriority() > priority) {
                        priority = rg.getPriority();
                        mina = rg.getId();
                    }

                }
                if (!mina.toLowerCase().contains("crime")) return;
                e.setCancelled(true);
                //eh na zona de crime do spawn
                //ta tentando roubar msm
                for (Map.Entry<String, Location> entry : Main.getInstance().getCrimeManager().getBlockRestore().entrySet()) {
                    if (entry.getValue().equals(e.getBlock().getLocation())) {
                        e.getPlayer().sendMessage(f("&cBloco regenerando.."));
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                e.getPlayer().sendBlockChange(e.getBlock().getLocation(), Material.BARRIER, (byte) 0);

                            }
                        }.runTaskLater(Main.getInstance(), 2L);
                        return;
                    }
                }
                Main.getInstance().getCrimeManager().blockRestore.put(e.getPlayer().getName(), e.getBlock().getLocation());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        e.getPlayer().sendBlockChange(e.getBlock().getLocation(), Material.BARRIER, (byte) 0);

                    }
                }.runTaskLater(Main.getInstance(), 2L);
                commited = true;
                Main.getInstance().getCrimeManager().addCrime(e.getPlayer().getName(), null, Objective.BLOCK_BREAK);
                Main.getInstance().getMissionsManager().updatePlayerObjective(e.getPlayer(), Objective.BLOCK_BREAK, 1);

            }
        }




        //todo verificar se eh crime

    }


    @EventHandler
    public void onPve(KillPVEEvent e) {
        if (day()) return;
        if (e.getPlayer() == null) return;
        if (e.getPlayer().getLocation().distance(e.getEntity().getLocation()) > 5) return;
        if (!Main.getInstance().getMissionsManager().playerHaveObjective(e.getPlayer(), Objective.KILL_MOBS)) return;
        ApplicableRegionSet set = WGBukkit.getPlugin().getRegionManager(e.getPlayer().getWorld())
                .getApplicableRegions(e.getLocation());
        String mina = "";
        int priority = 0;
        for (ProtectedRegion rg : set.getRegions()) {
            if (rg.getPriority() > priority) {
                priority = rg.getPriority();
                mina = rg.getId();
            }

        }
        if (mina == null || !mina.toLowerCase().contains("mina")) return;
        //todo verificar se eh crime
        Main.getInstance().getCrimeManager().addCrime(e.getPlayer().getName(), null, Objective.KILL_MOBS);
        Main.getInstance().getMissionsManager().updatePlayerObjective(e.getPlayer(), Objective.KILL_MOBS, e.getQuantidade());
    }


}
