package com.fendyk.commands;

import com.fendyk.DTOs.*;
import com.fendyk.DTOs.updates.UpdateMinecraftUserDTO;
import com.fendyk.Main;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RewardCommands {

    public RewardCommands(Main server) {
        new CommandAPICommand("reward")
                .withSubcommand(new CommandAPICommand("claim")
                        .executes((sender, args) -> {
                            Player player = (Player) sender;

                            MinecraftUserDTO minecraftUserDTO = server.getApi().getMinecraftUserAPI().get(player.getUniqueId());
                            if(minecraftUserDTO == null) {
                                player.sendMessage("Could not find your minecraft user account, try again.");
                                return;
                            }

                            double quanta = 0;
                            double crateKeys = 0;

                            UpdateMinecraftUserDTO update = new UpdateMinecraftUserDTO();
                            List<SubscriptionRewardDTO> unclaimedSubscriptionRewards = minecraftUserDTO.getSubscriptionRewards().stream().filter(s -> !s.isClaimed()).toList();

                            if(unclaimedSubscriptionRewards.size() < 1) {
                                player.sendMessage("We've detected no open subscription rewards. Maybe you've already claimed them all.");
                                return;
                            }

                            // Iterate over the subscription rewards that are open
                            for(SubscriptionRewardDTO sub : unclaimedSubscriptionRewards) {
                                quanta += sub.getQuanta();
                                crateKeys += sub.getCrateKeys();
                                update.getClaimSubscriptionRewards().add(sub.getCreatedAt().toString());
                            }

                            if(quanta > 0) server.getApi().getMinecraftUserAPI().depositBalance(player, new BigDecimal(quanta));
                            player.sendMessage(ChatColor.GREEN + "You've received " + quanta + " $QUANTA.");

                            if(crateKeys > 0) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate key give " + player.getName() + " diamond " + crateKeys);
                            player.sendMessage(ChatColor.GREEN + "You've received " + crateKeys + " crate keys.");

                            // Make sure we're updating the 'isClaimed' boolean
                            server.getApi().getMinecraftUserAPI().update(player.getUniqueId(), update);

                        })
                )
                .withSubcommand(new CommandAPICommand("create")
                        .withPermission(CommandPermission.OP)
                        .executesPlayer((player, args) -> {
                            UUID uuid = player.getUniqueId();

                            MinecraftUserDTO minecraftUserDTO = server.getApi().getMinecraftUserAPI().get(uuid);
                            if(minecraftUserDTO == null) {
                                player.sendMessage("Could not find your minecraft user account, try again.");
                                return;
                            }

                            UpdateMinecraftUserDTO update = new UpdateMinecraftUserDTO();
                            SubscriptionRewardDTO subscriptionRewardDTO = new SubscriptionRewardDTO();
                            subscriptionRewardDTO.setClaimed(false);
                            subscriptionRewardDTO.setQuanta(100);
                            subscriptionRewardDTO.setCrateKeys(10);
                            subscriptionRewardDTO.setCreatedAt(new DateTime());
                            update.getPushSubscriptionRewards().add(subscriptionRewardDTO);

                            MinecraftUserDTO minecraftUserDTO1 = server.getApi().getMinecraftUserAPI().update(uuid, update);

                            if(minecraftUserDTO1 == null) {
                                player.sendMessage(ChatColor.RED + "Could not update minecraft user");
                                return;
                            }

                            player.sendMessage(ChatColor.GREEN  + "Successfully created subscriptionReward");

                        })
                )
                .register();
    }

}
