package com.fendyk.commands;

import com.fendyk.DTOs.*;
import com.fendyk.DTOs.updates.UpdateMinecraftUserDTO;
import com.fendyk.Main;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
                            ArrayList<KitDTO> kits = new ArrayList<>();

                            UpdateMinecraftUserDTO update = new UpdateMinecraftUserDTO();
                            List<SubscriptionRewardDTO> subscriptions = minecraftUserDTO.getSubscriptionRewards().stream().filter(s -> !s.isClaimed()).toList();
                            if(subscriptions.size() > 0) {
                                for(SubscriptionRewardDTO sub : subscriptions) {
                                    quanta += sub.getQuanta();
                                    crateKeys += sub.getCrateKeys();
                                    kits.addAll(sub.getKits());
                                    update.getClaimSubscriptionRewards().add(sub.getCreatedAt());
                                }

                                // Make sure we're updating the 'isClaimed' boolean
                                server.getApi().getMinecraftUserAPI().update(player.getUniqueId(), update);
                            }

                            server.getApi().getMinecraftUserAPI().depositBalance(player, new BigDecimal(quanta));
                            server.getServer().dispatchCommand(Bukkit.getConsoleSender(), "/crate key give <player> <keyId> [amount]");

                            player.sendMessage("You've received " + quanta + " $QUANTA.");
                            player.sendMessage("You've received " + crateKeys + " crate keys.");
                            for(KitDTO kit : kits) {
                                player.sendMessage("You've received " + kit.getAmount() + " " + kit.getName() + " kit(s).");
                            }

                        })
                )
                .register();
    }

}
