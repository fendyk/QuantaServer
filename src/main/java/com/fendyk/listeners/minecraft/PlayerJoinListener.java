package com.fendyk.listeners.minecraft;

import com.fendyk.DTOs.MinecraftUserDTO;
import com.fendyk.DTOs.SubscriptionRewardDTO;
import com.fendyk.Main;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class PlayerJoinListener implements Listener {
    Main server;
    public PlayerJoinListener(Main server) {
        this.server = server;
    }
    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World world = Bukkit.getWorld(server.getServerConfig().getWorldName());

        User luckPermsUser = server.getLuckPermsApi().getPlayerAdapter(Player.class).getUser(player);

        Collection<Group> inheritedGroups = luckPermsUser.getInheritedGroups(luckPermsUser.getQueryOptions());
        boolean hasDefaultRank = inheritedGroups.stream().anyMatch(g -> g.getName().equals("default"));
        boolean hasBarbarianRank = inheritedGroups.stream().anyMatch(g -> g.getName().equals("barbarian"));
        boolean hasCitizenRank = inheritedGroups.stream().anyMatch(g -> g.getName().equals("citizen"));
        boolean hasHeroRank = inheritedGroups.stream().anyMatch(g -> g.getName().equals("hero"));

        if(hasDefaultRank && !player.isOp()) {
            player.teleport(new Location(world, 0,173,0));
            server.getFrozenPlayers().add(player.getUniqueId());
            player.sendMessage("You've been frozen because you're not authorized to the server.");
        }
        else {
            server.getFrozenPlayers().remove(player.getUniqueId());
        }

        MinecraftUserDTO minecraftUserDTO = server.getApi().getMinecraftUserAPI().get(player.getUniqueId());

        Bukkit.getLogger().info("HIII");

        // Verify if user still has a reward that needs to be claimed
        if(minecraftUserDTO != null) {
            Bukkit.getLogger().info("Found it");
            List<SubscriptionRewardDTO> subscriptions = minecraftUserDTO.getSubscriptionRewards().stream().filter(s -> !s.isClaimed()).toList();

            Bukkit.getLogger().info(subscriptions.size() + " :");
            if(subscriptions.size() > 0) {
                player.sendMessage("You have unclaimed rewards awaiting to be redeemed. To claim your reward, type /claim");
            }
        }

    }

}
