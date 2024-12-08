package com.mguhc;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.mguhc.game.UhcGame;
import com.mguhc.listeners.ModItemListener;
import com.mguhc.listeners.PlayerListener;
import com.mguhc.player.PlayerManager;
import com.mguhc.roles.RoleManager;
import com.mguhc.scenario.ScenarioManager;

public class UhcAPI extends JavaPlugin implements Listener {
    private PlayerManager playermanager;
    private UhcGame uhcgame;
    private RoleManager roleManager;
    private static UhcAPI instance;
	private ScenarioManager scenariomanager;

    @Override
    public void onEnable() {
        instance = this;
        playermanager = new PlayerManager();
        uhcgame = new UhcGame();
        roleManager = new RoleManager();
        scenariomanager = new ScenarioManager();
        
        // Enregistrer l'écouteur
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(uhcgame), this);
        Bukkit.getPluginManager().registerEvents(new ModItemListener(), this);
    }

    public static UhcAPI getInstance() {
        return instance;
    }
    
    public UhcGame getUhcGame() {
        return uhcgame;
    }

    public PlayerManager getPlayerManager() {
        return playermanager;
    }
    
    public RoleManager getRoleManager() {
        return roleManager;
    }
    
    public ScenarioManager getScenarioManager() {
    	return scenariomanager;
    }
}