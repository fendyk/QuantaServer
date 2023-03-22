package com.fendyk.commands;

import com.fendyk.Main;
import com.sk89q.minecraft.util.commands.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class EconomyCommands {
    Main main = Main.getInstance();

    public EconomyCommands() {
        new CommandAPICommand("pay")
                .withArguments(new PlayerArgument("player"))
                .withArguments(new DoubleArgument("amount"))
                .executes((sender, args) -> {
                    Player fromPlayer = (Player) sender;
                    OfflinePlayer toPlayer = (Player) args[0];
                    Double amount = (Double) args[1];

                    BigDecimal balance = main.getApi().getMinecraftUserAPI().getPlayerBalance(fromPlayer.getUniqueId());

                    if(balance == null || balance.doubleValue() < amount) {
                        fromPlayer.sendMessage("You dont have enough money to send.");
                        return;
                    }
                    else if(balance.doubleValue() < 0) {
                        fromPlayer.sendMessage("Cannot send money without money, makes sense right.. right?");
                        return;
                    }

                    boolean isWithdrawn = main.getApi().getMinecraftUserAPI().withDrawBalance(fromPlayer, new BigDecimal(amount));
                    boolean isDeposited = main.getApi().getMinecraftUserAPI().depositBalance(toPlayer, new BigDecimal(amount));
                    if(!isWithdrawn || !isDeposited) {
                        fromPlayer.sendMessage("Looks like something went wrong. Maybe you dont have enough $Quanta? If you believe this is an error, contact support.");
                        return;
                    }
                    fromPlayer.sendMessage("Your have payed " + amount + " to " + toPlayer.getName());

                    if(toPlayer.isOnline() && toPlayer.getPlayer() != null) {
                        toPlayer.getPlayer().sendMessage("Your have received " + amount + " from " + fromPlayer.getName());
                    }
                })
                .register();
    }


}
