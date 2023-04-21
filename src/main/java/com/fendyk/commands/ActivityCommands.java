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
                        ActivityDTO activityDTO = activitiesDTO.time;
                        player.sendMessage("Name: " + activityDTO.name);
                        player.sendMessage("Earned: " + activityDTO.earnings);
                        player.sendMessage("Amount: " + activityDTO.quantity);
                        player.sendMessage("");
                    })
                )
                .withSubcommand(new CommandAPICommand("pve")
                        .executes((sender, args) -> {
                            Player player = (Player) sender;

                            ActivitiesDTO activitiesDTO = api.getActivitiesAPI().redis.get(player.getUniqueId());
                            if(activitiesDTO == null || activitiesDTO.pve == null || activitiesDTO.pve.size() < 1) {
                                player.sendMessage("Could not find PVE activities.");
                                return;
                            }

                            for(ActivityDTO activityDTO : activitiesDTO.pve) {
                                player.sendMessage("Name: " + activityDTO.name);
                                player.sendMessage("Earned: " + activityDTO.earnings);
                                player.sendMessage("Amount: " + activityDTO.quantity);
                                player.sendMessage("");
                            }
                        })
                )
                .withSubcommand(new CommandAPICommand("pvp")
                        .executes((sender, args) -> {
                            Player player = (Player) sender;

                            ActivitiesDTO activitiesDTO = api.getActivitiesAPI().redis.get(player.getUniqueId());
                            if(activitiesDTO == null || activitiesDTO.pvp == null || activitiesDTO.pvp.size() < 1) {
                                player.sendMessage("Could not find PVP activities.");
                                return;
                            }

                            for(ActivityDTO activityDTO : activitiesDTO.pvp) {
                                player.sendMessage("Name: " + activityDTO.name);
                                player.sendMessage("Earned: " + activityDTO.earnings);
                                player.sendMessage("Amount: " + activityDTO.quantity);
                                player.sendMessage("");
                            }
                        })
                )
                .withSubcommand(new CommandAPICommand("mining")
                        .executes((sender, args) -> {
                            Player player = (Player) sender;

                            ActivitiesDTO activitiesDTO = api.getActivitiesAPI().redis.get(player.getUniqueId());
                            if(activitiesDTO == null || activitiesDTO.mining == null || activitiesDTO.mining.size() < 1) {
                                player.sendMessage("Could not find MINING activities.");
                                return;
                            }

                            for(ActivityDTO activityDTO : activitiesDTO.mining) {
                                player.sendMessage("Name: " + activityDTO.name);
                                player.sendMessage("Earned: " + activityDTO.earnings);
                                player.sendMessage("Amount: " + activityDTO.quantity);
                                player.sendMessage("");
                            }
                        })
                )
                .register();
    }

}