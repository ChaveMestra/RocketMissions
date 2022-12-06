package com.rocketmc.quests.crimes;

import com.rocketmc.quests.Main;
import com.rocketmc.quests.events.PlayerCrimeEvent;
import com.rocketmc.quests.missions.Objective;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

import static com.rocketmc.quests.utils.ChatUtils.f;
import static com.rocketmc.quests.utils.MsgCenter.broadcastMessage;

@Getter
public class CrimeManager {


    public HashMap<String, Long> commitingCrime;
    public HashMap<String, Location> blockRestore;
    public long timeTillRemove = 120;
    public boolean night;

    public CrimeManager() {
        blockRestore = new HashMap<>();
        commitingCrime = new HashMap<>();
        startCheckTask();
        night = false;
        CheckTime();
    }

    public static boolean day() {
        long time = Bukkit.getServer().getWorld("world").getTime();
        return time < 12300 || time > 23850;
    }

    public void startCheckTask() {

        new BukkitRunnable() {
            int seconds = 0;

            @Override
            public void run() {
                seconds++;
                String nomes = "";

                for (Map.Entry<String, Long> crimes : commitingCrime.entrySet()) {
                    nomes += crimes.getKey() + " ";
                    if (Bukkit.getPlayer(crimes.getKey()) == null) {
                        commitingCrime.replace(crimes.getKey(), System.currentTimeMillis() / 1000 + timeTillRemove);
                    }
                    if (crimes.getValue() <= System.currentTimeMillis() / 1000) {
                        resetCrimeCooldown(crimes.getKey(), false);
                        break;
                        //todo arruamr isso, fiz essa gambiarra soh pra nao dar concurrent modification
                    }
                }
                if (seconds == 60) {
                    if (commitingCrime.size() > 0) {
                        broadcastMessage(f(" "));
                        broadcastMessage(f("&4&l&nCRIMES"));
                        broadcastMessage(" ");
                        broadcastMessage(f("&6Jogadores atualmente fora da lei:"));
                        Bukkit.broadcastMessage(f("&c" + nomes));
                    }
                    seconds = 0;

                }

            }
        }.runTaskTimer(Main.getInstance(), 0, 20l);
    }

    public void resetCrimeCooldown(String playerName, boolean paid) {
        if (blockRestore.containsKey(playerName)) {
            for (Map.Entry<String, Location> blocks : blockRestore.entrySet()) {
                if (blocks.getKey().equals(playerName)) {
                    if (Bukkit.getPlayer(playerName) != null) {
                        Bukkit.getPlayer(playerName).sendBlockChange(blocks.getValue(), Material.OBSIDIAN, (byte) 0);
                    }
                }
            }
            blockRestore.remove(playerName);
        }
        commitingCrime.remove(playerName);

        broadcastMessage(f(" "));
        broadcastMessage(f("&4&l&nCRIMES"));
        broadcastMessage(" ");
        if (!paid) {
            broadcastMessage(f("&E" + playerName + " &cnao pagou pelos crimes!"));
            broadcastMessage("");
            if (Bukkit.getPlayer(playerName) != null) {
                Bukkit.getPlayer(playerName).sendMessage(f("&aVoce se safou dos crimes cometidos.."));
            }
        } else {
            broadcastMessage(f("&E" + playerName + " &4&lPAGOU SEUS CRIMES COM SUA VIDA!"));
            broadcastMessage("");
        }
    }


    public void resetCrimes() {
            for (Map.Entry<String, Location> blocks : blockRestore.entrySet()) {
                    if (Bukkit.getPlayer(blocks.getKey()) != null) {
                        Bukkit.getPlayer(blocks.getKey()).sendBlockChange(blocks.getValue(), Material.OBSIDIAN, (byte) 0);
                    }
                }
            blockRestore.clear();
        commitingCrime.clear();
    }

    public void addCrime(String playerName, String plotOwner, Objective objective) {

        if (commitingCrime.containsKey(playerName)) {
            commitingCrime.replace(playerName, System.currentTimeMillis() / 1000 + timeTillRemove);
        } else {
            commitingCrime.put(playerName, System.currentTimeMillis() / 1000 + timeTillRemove);
            broadcastMessage(f(" "));
            broadcastMessage(f("&4&l&nCRIMES"));
            broadcastMessage(" ");
            broadcastMessage(f("&E" + playerName + " &festa &ccometendo um crime!"));
            if (plotOwner != null) {
                broadcastMessage(f("&aROUBANDO PLOT DO JOGADOR &6" + plotOwner));
            } else {
                broadcastMessage(f("&aCrime: &6" + objective.getDescricao()));
            }
            broadcastMessage(f("&6PVP liberado contra ele por 120s!"));
            broadcastMessage("");
            if (Bukkit.getPlayer(playerName) != null) {
                Bukkit.getPlayer(playerName).setFlying(false);
                Bukkit.getPlayer(playerName).sendMessage(f("&cVoce é um fora da lei. Tente nao morrer por &e120s"));
            }
        }
        if (Bukkit.getPlayer(playerName) == null) {
            System.out.println("tentei add crime pra player offline: " + playerName + " q porra eh essa");
            return;
        }
        PlayerCrimeEvent playerCrimeEvent = new PlayerCrimeEvent(Bukkit.getPlayer(playerName));
        Bukkit.getPluginManager().callEvent(playerCrimeEvent);
    }

    public boolean isCommitingCrime(String playerName) {
        return commitingCrime.containsKey(playerName);
    }

    public void CheckTime() {
        //sincroniza os horarios

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    if (!world.getName().contains("world")) {
                        world.setTime(Bukkit.getWorld("world").getTime());
                    }
                }
                boolean dia = day();
                boolean tava = isNight();
                //FarmCraft.debug("DIA "+dia+" Tava "+tava);
                if (dia == tava) {
                    if (dia) {
                        Bukkit.broadcastMessage(("§aO dia vem com a justica, CRIMES PROIBIDOS!"));
                        resetCrimes();
                    } else {
                        Bukkit.broadcastMessage(("§cA noite cai e o crime se instala, CRIMES LIBERADOS"));
                    }
                    night = !dia;
                }
            }
        }, 0, 20 * 5);
    }


}
