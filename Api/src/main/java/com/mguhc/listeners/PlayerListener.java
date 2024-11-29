package com.mguhc.listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import com.mguhc.UhcAPI;
import com.mguhc.game.GamePhase;
import com.mguhc.game.UhcGame;
import com.mguhc.player.PlayerManager;
import com.mguhc.roles.RoleManager;
import com.mguhc.roles.UhcRole;
import com.mguhc.scenario.Scenario;
import com.mguhc.scenario.ScenarioManager;

public class PlayerListener implements Listener {
    
    private UhcGame uhcgame;
    private Map<Player, String> playerInputState = new HashMap<>();
    
    public PlayerListener(UhcGame uhcgame) {
        this.uhcgame = uhcgame;
    }

    @EventHandler
    private void OnJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World world = Bukkit.getWorld("world");
        GamePhase currentphase = uhcgame.getCurrentPhase();
        RoleManager roleManager = UhcAPI.getInstance().getRoleManager();
        PlayerManager playerManager = UhcAPI.getInstance().getPlayerManager();
        
        if (currentphase.getName().equals("Waiting")) {
            player.setMaxHealth(20);
            player.setHealth(20);
            player.setSaturation(20);
            player.getInventory().clear();
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(new Location(world, 0, 201, 0));
            if(roleManager.getRole(playerManager.getPlayer(player)) != null) {
                roleManager.removeRole(playerManager.getPlayer(player));
            }
            playerManager.addPlayer(player);
            
            // Vérifier si le joueur a la permission ou est op
            if (player.hasPermission("api.host") || player.isOp() && currentphase.getName() == "Waiting") {
                // Créer une étoile du Nether nommée "config"
                ItemStack netherStar = new ItemStack(Material.NETHER_STAR);
                ItemMeta meta = netherStar.getItemMeta();
                meta.setDisplayName(ChatColor.RED + "Config");
                netherStar.setItemMeta(meta);
                player.getInventory().addItem(netherStar); // Donner l'étoile du Nether au joueur
                player.updateInventory();
            }
        }
    }
    
    @EventHandler
    private void OnInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Vérifier si l'item est présent et a un nom d'affichage
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            // Vérifier si le nom d'affichage est "Config"
            if (item.getItemMeta().getDisplayName().equals(ChatColor.RED + "Config")) {
                // Ouvrir l'inventaire de configuration
                openConfigInventory(player);
            }
        }
    }

    private void openConfigInventory(Player player) {
        // Créer un inventaire de configuration de 54 slots
        Inventory configInventory = Bukkit.createInventory(null, 54, ChatColor.GREEN + "Configuration");
        
        // Créer l'item pour mettre en mode meetup
        ItemStack meetupItem = new ItemStack(Material.DIAMOND_SWORD, 1);
        ItemMeta meetupMeta = meetupItem.getItemMeta();
        meetupMeta.setDisplayName(ChatColor.GREEN + "Mode Mettup");
        meetupItem.setItemMeta(meetupMeta);

        // Créer l'item pour lancer la partie
        ItemStack startGameItem = new ItemStack(Material.WOOL, 1, (short) 5); // 5 correspond à la couleur verte
        ItemMeta startGameMeta = startGameItem.getItemMeta();
        if (startGameMeta != null) {
            startGameMeta.setDisplayName(ChatColor.GREEN + "Lancer la Partie");
            startGameItem.setItemMeta(startGameMeta);
        }

        // Créer l'item pour le mode de jeu
        ItemStack gameModeItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta gameModeMeta = gameModeItem.getItemMeta();
        if (gameModeMeta != null) {
            gameModeMeta.setDisplayName(ChatColor.RED + "Mode de jeu");
            gameModeItem.setItemMeta(gameModeMeta);
        }

        // Créer l'item pour la bordure
        ItemStack borderItem = new ItemStack(Material.BEACON);
        ItemMeta borderMeta = borderItem.getItemMeta();
        if (borderMeta != null) {
            borderMeta.setDisplayName(ChatColor.BLUE + "Bordure");
            borderItem.setItemMeta(borderMeta);
        }

        // Créer l'item pour ouvrir le menu des scénarios
        ItemStack scenarioItem = new ItemStack(Material.BOOK);
        ItemMeta scenarioMeta = scenarioItem.getItemMeta();
        if (scenarioMeta != null) {
            scenarioMeta.setDisplayName(ChatColor.GOLD + "Scénarios");
            scenarioMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Cliquez pour gérer les scénarios"));
            scenarioItem.setItemMeta(scenarioMeta);
        }

        // Ajouter les items à l'inventaire
        configInventory.setItem(3, meetupItem);
        configInventory.setItem(4, startGameItem);
        configInventory.setItem(31, gameModeItem);
        configInventory.setItem(13, borderItem);
        configInventory.setItem(22, scenarioItem); // Position pour l'item Scénarios

        // Ouvrir l'inventaire pour le joueur
        player.openInventory(configInventory);
    }

    // Écouter l'événement de clic dans l'inventaire
    @EventHandler
    public void onConfigInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GREEN + "Configuration")) {
            event.setCancelled(true); // Annuler l'événement pour éviter de déplacer les items

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            
            if(clickedItem != null && clickedItem.getType() == Material.DIAMOND_SWORD && 
               clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Mode Mettup")) {
            	if (uhcgame.getMettup() == true) {
            		uhcgame.setMettup(false);
            		player.sendMessage("Meetup Désactivé");
            	}
            	if (uhcgame.getMettup() == false) {
            		uhcgame.setMettup(true);
            		player.sendMessage("Meetup Activé");
            	}
            }

            // Vérifier si l'item cliqué est celui pour lancer la partie
            if (clickedItem != null && clickedItem.getType() == Material.WOOL && 
                clickedItem.getDurability() == 5 && // Vérifier que la couleur est verte
                clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Lancer la Partie")) {
            	player.sendMessage(ChatColor.GREEN + "La partie est lancé");
            	uhcgame.startGame();
                player.closeInventory(); // Fermer l'inventaire après avoir lancé la partie
            }
            if (clickedItem != null && clickedItem.getType() == Material.NETHER_STAR && 
                clickedItem.getItemMeta().getDisplayName().equals(ChatColor.RED + "Mode de jeu")) {
            	openGameModeInventory(player);
            }
            if (clickedItem != null && clickedItem.getType() == Material.BEACON && 
                clickedItem.getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Bordure")) {
                openBorderInventory(player);
            }
            if (clickedItem != null && clickedItem.getType() == Material.BOOK && 
                    clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Scénarios")) {
                    openScenarioInventory(player);
            }
        }
    }
    
    private void openScenarioInventory(Player player) {
        Inventory scenarioInventory = Bukkit.createInventory(null, 27, ChatColor.GREEN + "Gérer les Scénarios");

        // Ajouter chaque scénario à l'inventaire
        for (Scenario scenario : UhcAPI.getInstance().getScenarioManager().getScenarios()) {
            ItemStack scenarioItem = new ItemStack(Material.PAPER);
            ItemMeta meta = scenarioItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(scenario.getName());
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Cliquez pour " + (UhcAPI.getInstance().getScenarioManager().isScenarioActive(scenario.getName()) ? "désactiver" : "activer"));
                scenarioItem.setItemMeta(meta);
            }
            scenarioInventory.addItem(scenarioItem);
        }

        // Ouvrir l'inventaire pour le joueur
        player.openInventory(scenarioInventory);
    }

    private void openBorderInventory(Player player) {
        // Créer un inventaire de configuration de 27 slots
        Inventory borderInventory = Bukkit.createInventory(null, 27, ChatColor.GREEN + "Configurer la Bordure");

        // Créer un item pour définir la taille de la bordure
        ItemStack borderSizeItem = new ItemStack(Material.DIAMOND);
        ItemMeta borderSizeMeta = borderSizeItem.getItemMeta();
        if (borderSizeMeta != null) {
            borderSizeMeta.setDisplayName(ChatColor.YELLOW + "Taille de la Bordure");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Cliquez pour définir la taille de la bordure.");
            borderSizeMeta.setLore(lore);
            borderSizeItem.setItemMeta(borderSizeMeta);
        }
        borderInventory.setItem(11, borderSizeItem); // Position 11

        // Créer un item pour définir le timer de la bordure
        ItemStack borderTimerItem = new ItemStack(Material.COMPASS);
        ItemMeta borderTimerMeta = borderTimerItem.getItemMeta();
        if (borderTimerMeta != null) {
            borderTimerMeta.setDisplayName(ChatColor.YELLOW + "Timer de la Bordure");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Cliquez pour définir le timer de la bordure.");
            borderTimerMeta.setLore(lore);
            borderTimerItem.setItemMeta(borderTimerMeta);
        }
        borderInventory.setItem(15, borderTimerItem); // Position 15

        // Ouvrir l'inventaire pour le joueur
        player.openInventory(borderInventory);
    }

    // Écouter l'événement de clic dans l'inventaire de la bordure
    @EventHandler
    public void onBorderInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GREEN + "Configurer la Bordure")) {
            event.setCancelled(true); // Annuler l'événement pour éviter de déplacer les items

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            // Vérifier si l'item cliqué est celui pour définir la taille de la bordure
            if (clickedItem != null && clickedItem.getType() == Material.DIAMOND && 
                clickedItem.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Taille de la Bordure")) {
                player.sendMessage(ChatColor.GREEN + "Veuillez entrer la taille de la bordure dans le chat (en blocs) :");
            }

            // Vérifier si l'item cliqué est celui pour définir le timer de la bordure
            if (clickedItem != null && clickedItem.getType() == Material.COMPASS && 
                clickedItem.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Timer de la Bordure")) {
                player.sendMessage(ChatColor.GREEN + "Veuillez entrer le timer de la bordure en secondes :");
            }
        }
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // Vérifier si le joueur est en train de définir la taille de la bordure
        if (playerInputState.containsKey(player) && playerInputState.get(player).equals("borderSize")) {
            try {
                int borderSize = Integer.parseInt(message);
                uhcgame.setborderSize(borderSize); // Mettre à jour la taille de la bordure
                player.sendMessage(ChatColor.GREEN + "Taille de la bordure définie à " + borderSize + " blocs.");
                playerInputState.remove(player); // Réinitialiser l'état
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Veuillez entrer un nombre valide pour la taille de la bordure.");
            }
            event.setCancelled(true); // Annuler l'événement pour éviter d'afficher le message dans le chat
        }

        // Vérifier si le joueur est en train de définir le timer de la bordure
        if (playerInputState.containsKey(player) && playerInputState.get(player).equals("borderTimer")) {
            try {
                int borderTimer = Integer.parseInt(message);
                uhcgame.setborderTimer(borderTimer); // Mettre à jour le timer de la bordure
                player.sendMessage(ChatColor.GREEN + "Timer de la bordure défini à " + borderTimer + " secondes.");
                playerInputState.remove(player); // Réinitialiser l'état
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Veuillez entrer un nombre valide pour le timer de la bordure.");
            }
            event.setCancelled(true); // Annuler l'événement pour éviter d'afficher le message dans le chat
        }
    }

	private void openGameModeInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.GREEN + "Configurer les Rôles");

        for (UhcRole role : UhcAPI.getInstance().getRoleManager().getValidRoles()) {
            ItemStack roleItem = new ItemStack(Material.PAPER); // Utilisez un item approprié
            ItemMeta meta = roleItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(role.getName()); // Assurez-vous que UhcRole a une méthode getName()
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Cliquez pour " + (UhcAPI.getInstance().getRoleManager().getActiveRoles().contains(role) ? "désactiver" : "activer"));
                meta.setLore(lore);
                roleItem.setItemMeta(meta);
            }
            inventory.addItem(roleItem);
        }
        player.openInventory(inventory);
	}
	
    @EventHandler
    public void onGameModeInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GREEN + "Configurer les Rôles")) {
            event.setCancelled(true); // Annuler l'événement pour éviter de déplacer les items

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            List<UhcRole> activeRoles = UhcAPI.getInstance().getRoleManager().getActiveRoles();

            if (clickedItem != null && clickedItem.getType() == Material.PAPER) {
                String roleName = clickedItem.getItemMeta().getDisplayName();
                UhcRole clickedRole = findRoleByName(roleName); // Méthode pour trouver le rôle par son nom

                if (clickedRole != null) {
                    if (activeRoles.contains(clickedRole)) {
                        activeRoles.remove(clickedRole);
                        player.sendMessage(ChatColor.RED + "Rôle " + roleName + " désactivé.");
                    } else {
                        activeRoles.add(clickedRole);
                        player.sendMessage(ChatColor.GREEN + "Rôle " + roleName + " activé.");
                    }

                    // Mettre à jour l'item dans l'inventaire
                    updateRoleItem(clickedItem, clickedRole);
                }
            }
        }
    }
    
    @EventHandler
    public void OnScenarioClick(InventoryClickEvent event) {
    	if (event.getView().getTitle().equals(ChatColor.GREEN + "Gérer les Scénarios")) {
            event.setCancelled(true); // Annuler l'événement pour éviter de déplacer les items
            
            ScenarioManager scenarioManager = UhcAPI.getInstance().getScenarioManager();
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            // Gérer les clics sur les items des scénarios
            if (clickedItem != null && clickedItem.getType() == Material.PAPER) {
                String scenarioName = clickedItem.getItemMeta().getDisplayName();
                if (scenarioManager.isScenarioActive(scenarioName)) {
                	scenarioManager.deactivateScenario(scenarioName);
                    player.sendMessage(ChatColor.RED + scenarioName + " désactivé !");
                } else {
                	scenarioManager.activateScenario(scenarioName);
                    player.sendMessage(ChatColor.GREEN + scenarioName + " activé !");
                }
                openScenarioInventory(player); // Réouvrir le menu des scénarios pour mettre à jour l'état
            }
        }
    }

    private UhcRole findRoleByName(String name) {
    	List<UhcRole> validRoles = UhcAPI.getInstance().getRoleManager().getValidRoles();
        for (UhcRole role : validRoles) {
            if (role.getName().equals(name)) {
                return role;
            }
        }
        return null;
    }

    private void updateRoleItem(ItemStack item, UhcRole role) {
        ItemMeta meta = item.getItemMeta();
        List<UhcRole> activeRoles = UhcAPI.getInstance().getRoleManager().getActiveRoles();
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            // Ajouter le statut du rôle dans la lore
            lore.add(ChatColor.GRAY + "Cliquez pour " + (activeRoles.contains(role) ? "désactiver" : "activer"));
            lore.add(activeRoles.contains(role) ? ChatColor.GREEN + "Rôle activé" : ChatColor.RED + "Rôle désactivé");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }
}