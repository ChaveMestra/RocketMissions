package com.rocketmc.quests.missions;

import lombok.Getter;

@Getter
public enum Objective {

    KILL_MOBS("Mate animais na mina PVP"),
    FARM_COLLECT("Roube plantacoes no plot de outros jogadores"),
    KILL_PLAYER("Cometa assassinato"),
    BLOCK_BREAK("Roube blocos da arena da cidade");

    private String descricao;

    Objective(String descricao) {
        this.descricao = descricao;
    }

    public static Objective getFromConfig(String configName) {
        for (Objective objective : Objective.values()) {
            if (configName.equalsIgnoreCase(objective.toString())) {
                return objective;
            }
        }
        return null;
    }

    public static void printValidObjetives() {
        System.out.println("Objetivos validos:");
        for (Objective objective1 : Objective.values()) {
            System.out.println(objective1.toString());
        }
    }

    public static Objective getFromLore(String lore) {
        for (Objective objective : Objective.values()) {
            if (objective.getDescricao().equals(lore)) {
                return objective;
            }
        }
        return null;
    }
}
