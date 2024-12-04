package com.mguhc.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ModItemListener implements Listener {
	
	@EventHandler
	private void OnInteract(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		Player player = event.getPlayer();
		player.sendMessage(player.getName() + "avez itéragie avec le " + item.getItemMeta().getDisplayName());
		Action action = event.getAction();
		if(item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Vanish")) {
			if(player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
				player.sendMessage(ChatColor.RED + "Vous avez désactivé la Vanish");
				player.removePotionEffect(PotionEffectType.INVISIBILITY);
			}
			else {
				player.sendMessage(ChatColor.GREEN + "Vous avez activé la Vanish");
				player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
			}
		}
		if(item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Tp")) {
			openTpInventory(player);
		}
		if(item.getItemMeta().getDisplayName().equals(ChatColor.stripColor("Warn")) && action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
			openWarnInventory(player);
		}
	}

	private void openWarnInventory(Player player) {
		// Créer un inventaire de 54 slots
        Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.GREEN + "Sélectionner un joueur");

        // Récupérer tous les joueurs en ligne
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            // Créer un item pour chaque joueur
            ItemStack playerItem = new ItemStack(Material.SKULL_ITEM, 1, (short) 3); // Utiliser une tête de joueur
            ItemMeta meta = playerItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(onlinePlayer.getName()); // Nom du joueur
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GREEN + "Cliquez pour avertir à ce joueur");
                meta.setLore(lore);
                playerItem.setItemMeta(meta);
            }
            // Ajouter l'item à l'inventaire
            inventory.addItem(playerItem);
        }
        // Ouvrir l'inventaire pour le joueur
        player.openInventory(inventory);
	}

	private void openTpInventory(Player player) {
		// Créer un inventaire de 54 slots
        Inventory inventory = Bukkit.createInventory(null, 36, ChatColor.GREEN + "Se téléporter à un joueur");

        // Récupérer tous les joueurs en ligne
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            // Créer un item pour chaque joueur
            ItemStack playerItem = new ItemStack(Material.SKULL_ITEM, 1, (short) 3); // Utiliser une tête de joueur
            ItemMeta meta = playerItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(onlinePlayer.getName()); // Nom du joueur
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GREEN + "Cliquez pour vous tp à ce joueur");
                meta.setLore(lore);
                playerItem.setItemMeta(meta);
            }
            // Ajouter l'item à l'inventaire
            inventory.addItem(playerItem);
        }
        // Ouvrir l'inventaire pour le joueur
        player.openInventory(inventory);
    }
	
	@EventHandler
	private void OnInventoryClick(InventoryClickEvent event) {
		Player clickedPlayer = null;
		Inventory inventory = event.getInventory();
		if(inventory.getName().equals(ChatColor.GREEN + "Se téléporter à un joueur")) {
			event.setCancelled(true);
			Player player = (Player) event.getWhoClicked();
			ItemStack item = event.getCurrentItem();
			if(item.getType() == Material.SKULL) {
				String name = item.getItemMeta().getDisplayName();
				clickedPlayer = Bukkit.getPlayer(name);
				player.teleport(clickedPlayer);
				player.sendMessage(ChatColor.GREEN + "Vous vous êtes Tp à " + name);
			}
		}
		if(inventory.getName().equals(ChatColor.GREEN + "Sélectionner un joueur")) {
			event.setCancelled(true);
			Player player = (Player) event.getWhoClicked();
			ItemStack item = event.getCurrentItem();
			if(item.getType() == Material.SKULL) {
				String name = item.getItemMeta().getDisplayName();
				clickedPlayer = Bukkit.getPlayer(name);
				openSanctionInventory(player, clickedPlayer);
			}
		}
		if(inventory.getName().equals(ChatColor.RED + "Sanction")) {
			Player player = (Player) event.getWhoClicked();
			ItemStack item = event.getCurrentItem();
			OnSanctionClick(player, item, clickedPlayer);
		}
	}

	private void openSanctionInventory(Player player, Player clickedPlayer) {
		Inventory inventory =  Bukkit.createInventory(null, 36, ChatColor.RED + "Sanction");
		
		ItemStack grp = new ItemStack(Material.PAPER);
		ItemMeta grp_meta = grp.getItemMeta();
		if(grp_meta != null) {
			grp_meta.setDisplayName("Non respect des groupes");
			grp.setItemMeta(grp_meta);
		}
		inventory.setItem(0, grp);
		
		ItemStack sanglier = new ItemStack(Material.PAPER);
		ItemMeta sanglier_meta = sanglier.getItemMeta();
		if(sanglier_meta != null) {
			sanglier_meta.setDisplayName("Sanglier");
			sanglier.setItemMeta(sanglier_meta);
		}
		inventory.setItem(1, sanglier);
		
		player.openInventory(inventory);
	}
	
	private void OnSanctionClick(Player player, ItemStack item, Player clickedPlayer) {
		if(item.getItemMeta().getDisplayName().equals("Non respect des groupes")) {
			clickedPlayer.sendMessage(ChatColor.RED + "Vous avez été averti pour le non respect des groupes par le modérateur : " + player.getName());
		}
		if(item.getItemMeta().getDisplayName().equals("Sanglier")) {
			clickedPlayer.sendMessage(ChatColor.RED + "Vous avez été averti pour un sanglier par le modérateur : " + player.getName());
		}
	}
}