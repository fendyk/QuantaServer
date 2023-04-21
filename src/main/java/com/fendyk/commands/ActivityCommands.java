package com.fendyk.commands;

import com.fendyk.API;
import com.fendyk.DTOs.ActivitiesDTO;
import com.fendyk.DTOs.ActivityDTO;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class ActivityCommands {

    public ActivityCommands(API api) {
        new CommandAPICommand("activities")
                .withAliases("activity")
                .withSubcommand(new CommandAPICommand("time")
                    .executes((sender, args) -> {
                        Player player = (Player) sender;

                        ActivitiesDTO activitiesDTO = api.getActivitiesAPI().redis.get(player.getUniqueId());
                        if(activitiesDTO == null) {
                            player.sendMessage("Could not find TIME activity.");
                            return;
                        }
                        ActivityDTO activityDTO = activitiesDTO.getTime();
                        player.sendMessage("Name: " + activityDTO.getName());
                        player.sendMessage("Earned: " + activityDTO.getEarnings());
                        player.sendMessage("Amount: " + activityDTO.getQuantity());
                        player.sendMessage("");
                    })
                )
                .withSubcommand(new CommandAPICommand("pve")
                        .executes((sender, args) -> {
                            Player player = (Player) sender;

                            ActivitiesDTO activitiesDTO = api.getActivitiesAPI().redis.get(player.getUniqueId());
                            if(activitiesDTO == null || activitiesDTO.getPve() == null || activitiesDTO.getPve().size() < 1) {
                                player.sendMessage("Could not find PVE activities.");
                                return;
                            }

                            for(ActivityDTO activityDTO : activitiesDTO.getPve()) {
                                player.sendMessage("Name: " + activityDTO.getName());
                                player.sendMessage("Earned: " + activityDTO.getEarnings());
                                player.sendMessage("Amount: " + activityDTO.getQuantity());
                                player.sendMessage("");
                            }
                        })
                )
                .withSubcommand(new CommandAPICommand("pvp")
                        .executes((sender, args) -> {
                            Player player = (Player) sender;

                            ActivitiesDTO activitiesDTO = api.getActivitiesAPI().redis.get(player.getUniqueId());
                            if(activitiesDTO == null || activitiesDTO.getPvp() == null || activitiesDTO.getPvp().size() < 1) {
                                player.sendMessage("Could not find PVP activities.");
                                return;
                            }

                            for(ActivityDTO activityDTO : activitiesDTO.getPvp()) {
                                player.sendMessage("Name: " + activityDTO.getName());
                                player.sendMessage("Earned: " + activityDTO.getEarnings());
                                player.sendMessage("Amount: " + activityDTO.getQuantity());
                                player.sendMessage("");
                            }
                        })
                )
                .withSubcommand(new CommandAPICommand("mining")
                        .executes((sender, args) -> {
                            Player player = (Player) sender;

                            ActivitiesDTO activitiesDTO = api.getActivitiesAPI().redis.get(player.getUniqueId());
                            if(activitiesDTO == null || activitiesDTO.getMining() == null || activitiesDTO.getMining().size() < 1) {
                                player.sendMessage("Could not find MINING activities.");
                                return;
                            }

                            for(ActivityDTO activityDTO : activitiesDTO.getMining()) {
                                player.sendMessage("Name: " + activityDTO.getName());
                                player.sendMessage("Earned: " + activityDTO.getEarnings());
                                player.sendMessage("Amount: " + activityDTO.getQuantity());
                                player.sendMessage("");
                            }
                        })
                )
                .register();
    }

}