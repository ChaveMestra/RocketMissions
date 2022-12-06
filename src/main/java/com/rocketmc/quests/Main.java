package com.rocketmc.quests;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.rocketmc.quests.commands.GiveCommand;
import com.rocketmc.quests.config.Config;
import com.rocketmc.quests.crimes.CrimeManager;
import com.rocketmc.quests.listeners.CompleteListener;
import com.rocketmc.quests.listeners.CrimeRelatedListener;
import com.rocketmc.quests.listeners.ObjectivesListener;
import com.rocketmc.quests.missions.MissionsManager;
import com.rocketmc.quests.placeholders.CrimePlaceholders;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import rocketlanguage.Language;

@Getter
public class Main extends JavaPlugin {

    @Getter
    public static Main instance;
    private MissionsManager missionsManager;
    private CrimeManager crimeManager;
    private PlotAPI plotAPI;
    private Config configuracao;
    private Language language;

    @Override
    public void onEnable() {
        instance = this;
        language = new Language();
        language.setupLanguage(this);
        this.configuracao = new Config();
        configuracao.setup(this);
        missionsManager = new MissionsManager();
        crimeManager = new CrimeManager();
        plotAPI = new PlotAPI();
        Bukkit.getPluginManager().registerEvents(new ObjectivesListener(), this);
        Bukkit.getPluginManager().registerEvents(new CompleteListener(), this);
        Bukkit.getPluginManager().registerEvents(new CrimeRelatedListener(), this);
        getCommand("givemission").setExecutor(new GiveCommand());
        new CrimePlaceholders(this, "crimes").hook();
    }

    @Override
    public void onDisable() {

    }
}
