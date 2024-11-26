package com.mguhc.roles;

import com.mguhc.player.UhcPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleManager {
    private Map<UhcPlayer, UhcRole> playerRoles;
    private List<UhcRole> validRoles; // Liste des rôles valides
    private List<UhcRole> activeRoles;

    public RoleManager() {
        playerRoles = new HashMap<>();
        validRoles = new ArrayList<>(); // Initialiser la liste des rôles valides
        activeRoles = new ArrayList<>();
    }

    public void assignRole(UhcPlayer player, UhcRole role) {
        playerRoles.put(player, role);
        player.getPlayer().sendMessage("Vous avez été assigné au rôle : " + role.getName());
    }

    public UhcRole getRole(UhcPlayer player) {
        return playerRoles.get(player);
    }

    public void removeRole(UhcPlayer player) {
        playerRoles.remove(player);
        player.getPlayer().sendMessage("Votre rôle a été retiré.");
    }

    public void listRoles() {
        for (Map.Entry<UhcPlayer, UhcRole> entry : playerRoles.entrySet()) {
            UhcPlayer player = entry.getKey();
            UhcRole role = entry.getValue();
            player.getPlayer().sendMessage(player.getPlayer().getName() + " a le rôle : " + role.getName());
        }
    }

    public List<UhcRole> getValidRoles() {
        return validRoles; // Méthode pour récupérer la liste des rôles valides
    }
    
    public List<UhcRole> getActiveRoles() {
    	return activeRoles;
    }

    // Méthode pour ajouter un rôle à la liste des rôles valides
    public void addRole(UhcRole role) {
        if (!validRoles.contains(role)) {
            validRoles.add(role);
        }
    }
}