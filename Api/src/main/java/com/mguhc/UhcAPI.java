package com.mguhc;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.mguhc .commands.AssignRoleCommand;
import com.mguhc.game.UhcGame;
import com.mguhc.listeners.PlayerListener;
import com.mguhc.player.PlayerManager;
import com.mguhc.roles.RoleManager;
import com.mguhc.roles.UhcRole;
import com.mguhc.scenario.ScenarioManager;

public class UhcAPI extends JavaPlugin implements Listener {
    private PlayerManager playermanager;
    private UhcGame uhcgame;
    private RoleManager roleManager;
    private static UhcAPI instance;
    private HashMap<String, String> roles;
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
        
        // Enregistrer les commands
        getCommand("giverole").setExecutor(new AssignRoleCommand());
        
        roles = new HashMap<>();
        roles.put("Warrior", "A strong fighter");
        roles.put("Archer", "A skilled marksman");
        
        initialiazeRole();
    }

    private void initialiazeRole() {
        for (Map.Entry<String, String> entry : roles.entrySet()) {
            String role = entry.getKey(); // Le rôle
            String description = entry.getValue(); // La description
            roleManager.addRole(new UhcRole(role, description));
        }
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