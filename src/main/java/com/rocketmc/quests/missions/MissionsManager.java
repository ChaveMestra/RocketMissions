package com.rocketmc.quests.missions;

import com.rocketmc.quests.Main;
import com.rocketmc.quests.inventory.InventoryUtils;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Getter
public class MissionsManager {

    ArrayList<Mission> loadedMissions;

    public MissionsManager() {
        loadMissions();
    }

    public void loadMissions() {
        this.loadedMissions = new ArrayList<>();
        FileConfiguration config = Main.getInstance().getConfiguracao().getConfig();
        for (String objective : config.getConfigurationSection("Objetivos").getKeys(false)) {
            if (Objective.getFromConfig(objective) == null) {
                System.out.println("Nao achei o objetivo " + objective + ".. pulando");
                Objective.printValidObjetives();
                continue;
            }
            Objective objectiveEnum = Objective.getFromConfig(objective);
            for (String rarity : config.getConfigurationSection("Objetivos." + objective + ".Raridades").getKeys(false)) {
                if (Rarity.getFromConfig(rarity) == null) {
                    System.out.println("Nao achei a raridade " + rarity + ".. pulando");
                    Rarity.printValidRarities();
                    continue;
                }
                Rarity rarityEnum = Rarity.getFromConfig(rarity);
                int goal = config.getInt("Objetivos." + objective + ".Raridades." + rarity + ".Quantidade");
                if (goal == 0) {
                    System.out.println("Quantidade do objetivo " + objective + " de raridade " + rarity + " invalida.. pulando");
                    continue;
                }
                List<String> rewards = config.getStringList("Objetivos." + objective + ".Raridades." + rarity + ".Recompensas");
                if (rewards.size() == 0) {
                    System.out.println("Recompensas do objetivo " + objective + " de raridade " + rarity + " invalida.. pulando");
                    continue;
                }
                List<String> comandos = config.getStringList("Objetivos." + objective + ".Raridades." + rarity + ".Comandos");
                if (comandos.size() == 0) {
                    System.out.println("Comandos do objetivo " + objective + " de raridade " + rarity + " invalida.. pulando");
                    continue;
                }
                Mission mission = new Mission(rarityEnum, objectiveEnum, goal, rewards, comandos);
                System.out.println("Registrando missao de objetivo " + objective + " com goal " + goal + " de raridade " + rarity);
                this.loadedMissions.add(mission);
            }
        }
        System.out.println("Missoes loadadas: " + this.loadedMissions.size());
    }


    public void updatePlayerObjective(Player player, Objective objective, int quantity) {
        for (Map.Entry<ItemStack, Mission> entry : InventoryUtils.getMissionsFromInventory(player).entrySet()) {
            if (entry.getValue().getObjective() == objective) {
                if (InventoryUtils.updateMissionItemStack(entry.getKey(), entry.getValue(), quantity)) {
                    player.updateInventory();
                    return;
                }
            }
        }
    }

    public boolean playerHaveObjective(Player player, Objective objective) {
        for (Map.Entry<ItemStack, Mission> entry : InventoryUtils.getMissionsFromInventory(player).entrySet()) {
            if (entry.getValue().getObjective().equals(objective)) {
                return true;
            }
        }
        return false;
    }

    public Mission getMission(String raridade) {
        ArrayList<Mission> selectedMissions = new ArrayList<>();
        for (Mission mission : this.getLoadedMissions()) {
            if (raridade.equalsIgnoreCase("comum") && mission.getRarity() == Rarity.COMUM) {
                selectedMissions.add(mission);
            }
            if (raridade.equalsIgnoreCase("incomum") && mission.getRarity() == Rarity.INCOMUM) {
                selectedMissions.add(mission);
            }
            if (raridade.equalsIgnoreCase("rara") && mission.getRarity() == Rarity.RARA) {
                selectedMissions.add(mission);
            }
            if (raridade.equalsIgnoreCase("epica") && mission.getRarity() == Rarity.EPICA) {
                selectedMissions.add(mission);
            }
            if (raridade.equalsIgnoreCase("lendaria") && mission.getRarity() == Rarity.LENDARIA) {
                selectedMissions.add(mission);
            }
        }
        if (selectedMissions.size() == 0) {
            Random random = new Random();
            int choice = random.nextInt(100);
            if (choice <= 60) {
                return getMission("comum");
            } else if (choice <= 80) {
                return getMission("incomum");
            } else if (choice <= 90) {
                return getMission("rara");
            } else if (choice <= 95) {
                return getMission("epica");
            } else {
                return getMission("lendaria");
            }
        }
        Random r = new Random();
        int choice = r.nextInt(selectedMissions.size());
        return selectedMissions.get(choice);


    }
}
