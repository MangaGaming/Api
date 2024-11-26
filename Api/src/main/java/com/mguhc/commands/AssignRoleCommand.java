package com.mguhc.commands;

import com.mguhc.UhcAPI;
import com.mguhc.player.UhcPlayer;
import com.mguhc.roles.UhcRole;
import com.mguhc.roles.RoleManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AssignRoleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Cette commande ne peut être exécutée que par un joueur.");
            return true;
        }

        Player player = (Player) sender;
        UhcPlayer uhcPlayer = UhcAPI.getInstance().getPlayerManager().getPlayer(player);
        if (uhcPlayer == null) {
            player.sendMessage("Vous n'êtes pas un joueur UHC.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("Veuillez spécifier un rôle à assigner.");
            return true;
        }

        String roleName = args[0];
        RoleManager roleManager = UhcAPI.getInstance().getRoleManager();
        UhcRole roleToAssign = null;

        // Vérifier si le rôle existe dans la liste des rôles valides
        for (UhcRole role : roleManager.getValidRoles()) {
            if (role.getName().equalsIgnoreCase(roleName)) {
                roleToAssign = role;
                break;
            }
        }

        if (roleToAssign == null) {
            player.sendMessage("Rôle invalide : " + roleName);
            return true;
        }

        roleManager.assignRole(uhcPlayer, roleToAssign);
        return true;
    }
}