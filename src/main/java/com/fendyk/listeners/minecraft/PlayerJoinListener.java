package com.fendyk.listeners.minecraft;

import com.fendyk.DTOs.MinecraftUserDTO;
import com.fendyk.DTOs.SubscriptionRewardDTO;
import com.fendyk.Main;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.luckperms.api.model.user.User;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.List;

public class PlayerJoinListener implements Listener {

    Main main = Main.instance;
    Main server;

    public PlayerJoinListener(Main server) {
        this.server = server;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World world = Bukkit.getWorld(server.serverConfig.getWorldName());
        Audience audience = server.adventure().player(player);
        User luckPermsUser = server.getLuckPermsApi().getPlayerAdapter(Player.class).getUser(player);
        String primaryGroup = luckPermsUser.getPrimaryGroup();

        // Give the player a starterpack
        if (!player.hasPlayedBefore()) {
            giveStarterKit(player);
            player.sendMessage(ChatColor.GREEN + "We've given you some starter items to start your adventure!");
        }

        if (primaryGroup.equalsIgnoreCase("default") && !player.isOp()) {
            player.teleport(main.serverConfig.getSpawnLocation());
            server.getFrozenPlayers().add(player.getUniqueId());
            player.sendMessage("You've been frozen because you're not authorized to the server.");
        } else {
            server.frozenPlayers.remove(player.getUniqueId());
        }

        MinecraftUserDTO minecraftUserDTO = server.api.minecraftUserAPI.get(player.getUniqueId());

        // Verify if user still has a reward that needs to be claimed
        if (minecraftUserDTO != null) {
            Bukkit.getLogger().info("Found it");
            List<SubscriptionRewardDTO> subscriptions = minecraftUserDTO.subscriptionRewards.stream().filter(s -> !s.isClaimed).toList();

            Bukkit.getLogger().info(subscriptions.size() + " :");
            if (subscriptions.size() > 0) {
                player.sendMessage("You have unclaimed rewards awaiting to be redeemed. To claim your reward, type /claim");
            }
        }

        // If player joins first time
        if (!player.hasPlayedBefore()) {
            // Finally show a title saying welcome!
            final Component mainTitle = Component.text("Welcome to QuantumCity,", NamedTextColor.AQUA);
            final Component subtitle = Component.text(player.getName(), NamedTextColor.WHITE);

            // Creates a simple title with the default values for fade-in, stay on screen and fade-out durations
            final Title title = Title.title(mainTitle, subtitle);

            // Send the title to your audience
            audience.showTitle(title);

            // Set join message
            final TextComponent msg = Component.text()
                    .color(NamedTextColor.AQUA)
                    .append(Component.text(player.getName() + " -> has joined the server for the first time, say hi!"))
                    .build();

            event.joinMessage(msg);
        } else {
            // Finally show a title saying welcome!
            final Component mainTitle = Component.text("Welcome back,", NamedTextColor.AQUA);
            final Component subtitle = Component.text(player.getName(), NamedTextColor.WHITE);

            // Creates a simple title with the default values for fade-in, stay on screen and fade-out durations
            final Title title = Title.title(mainTitle, subtitle);

            // Send the title to your audience
            audience.showTitle(title);

            // Set join message
            final TextComponent msg = Component.text()
                    .color(NamedTextColor.GRAY)
                    .append(Component.text(player.getName() + " -> has joined the server."))
                    .build();

            event.joinMessage(msg);
        }

        // Retrieve data (From redis) for using it as caching purposes later
        main.api.activitiesAPI.get(player);

        // Set Placeholder api values
        PlaceholderAPI.setPlaceholders(player, "%quantum_eco_balance%");
        PlaceholderAPI.setPlaceholders(player, "%quantum_activities_mining_daily_earned%");
        PlaceholderAPI.setPlaceholders(player, "%quantum_activities_pve_daily_earned%");
        PlaceholderAPI.setPlaceholders(player, "%quantum_activities_mining_daily_quantity%");
        PlaceholderAPI.setPlaceholders(player, "%quantum_activities_pve_daily_quantity%");
        //PlaceholderAPI.setPlaceholders(player, "%quantum_land_standing%");

    }

    public void giveStarterKit(Player player) {
        // Enchanted stone sword
        ItemStack stoneSword = new ItemStack(Material.STONE_SWORD);
        stoneSword.addEnchantment(Enchantment.DURABILITY, 3);

        // Steak
        ItemStack steak = new ItemStack(Material.COOKED_BEEF, 8);

        // Gray-dyed enchanted leather helmet
        ItemStack leatherHelmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta helmetMeta = (LeatherArmorMeta) leatherHelmet.getItemMeta();
        helmetMeta.setColor(Color.GRAY);
        helmetMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        leatherHelmet.setItemMeta(helmetMeta);

        // Gray-dyed enchanted leather chestplate
        ItemStack leatherChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) leatherChestplate.getItemMeta();
        chestplateMeta.setColor(Color.GRAY);
        chestplateMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        leatherChestplate.setItemMeta(chestplateMeta);

        // Gray-dyed enchanted leather leggings
        ItemStack leatherLeggings = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leatherLeggings.getItemMeta();
        leggingsMeta.setColor(Color.GRAY);
        leggingsMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        leatherLeggings.setItemMeta(leggingsMeta);

        // Gray-dyed enchanted leather boots
        ItemStack leatherBoots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) leatherBoots.getItemMeta();
        bootsMeta.setColor(Color.GRAY);
        bootsMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        leatherBoots.setItemMeta(bootsMeta);

        // Add items to the player's inventory
        player.getInventory().addItem(stoneSword, steak, leatherHelmet, leatherChestplate, leatherLeggings, leatherBoots);
    }

}
