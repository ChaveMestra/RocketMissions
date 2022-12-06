package com.rocketmc.quests.missions;

import lombok.Getter;

import static com.rocketmc.quests.utils.ChatUtils.f;

@Getter
public enum Rarity {

    COMUM(f("&f&lCRIME COMUM"), "&f"),
    INCOMUM(f("&a&lCRIME INCOMUM"), "&a"),
    RARA(f("&6&lCRIME RARO"), "&6"),
    EPICA(f("&d&lCRIME EPICO"), "&d"),
    LENDARIA(f("&c&lCRIME LENDARIO"), "&c");

    public String descricao;
    public String color;

    Rarity(String descricao, String color) {
        this.descricao = descricao;
        this.color = color;
    }

    public static Rarity getFromConfig(String configName) {
        for (Rarity rarity : Rarity.values()) {
            if (configName.equalsIgnoreCase(rarity.toString())) {
                return rarity;
            }
        }
        return null;
    }

    public static void printValidRarities() {
        System.out.println("Raridades validas:");
        for (Rarity rarity : Rarity.values()) {
            System.out.println(rarity.toString());
        }
    }

    public static Rarity getFromLore(String lore) {
        for (Rarity rarity : Rarity.values()) {
            if (lore.equals(rarity.getDescricao())) {
                return rarity;
            }
        }
        return null;
    }
}
