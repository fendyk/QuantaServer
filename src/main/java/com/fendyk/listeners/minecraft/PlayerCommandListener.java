package com.fendyk.listeners.minecraft;

import com.fendyk.Main;
import com.fendyk.configs.PricesConfig;
import com.fendyk.managers.ConfirmCommandManager;
import com.fendyk.utilities.Log;
import com.fendyk.utilities.PayableCommand;
import com.fendyk.utilities.RankConfiguration;
import com.fendyk.utilities.extentions.LuckPermsExtention;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class PlayerCommandListener implements Listener {

    Main main = Main.getInstance();
    PricesConfig pricesConfig = main.getPricesConfig();

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        // This method will be called when a player enters a command
        // into their chat box, but before the server executes the command
        String command = event.getMessage(); // Get the command entered by the player
        Player player = event.getPlayer(); // Get the player who entered the command
        String rankName = LuckPermsExtention.getHighestGroup(player);

        Log.info("Now: " + command);

        // Do something with the command and player
        // For example, you can cancel the command by calling event.setCancelled(true);

        // Split the command string into the command name and arguments
        String[] commandParts = command.split(" ");
        //String commandName = commandParts[0].substring(1); // Remove the slash from the command name
        String commandName = commandParts[0]; // Remove the slash from the command name
        String[] args = Arrays.copyOfRange(commandParts, 1, commandParts.length);

        int commandIndex = main.getPricesConfig().getCommandIndex(commandName, args);

        if (commandIndex >= 0) {
            Log.success("Payed command found!");

            PayableCommand payableCommand = pricesConfig.getCommandByIndex(commandIndex);
            RankConfiguration configuration = main.getRanksConfig().getRankConfiguration(rankName);

            if(configuration == null || payableCommand == null) {
                player.sendMessage(ChatColor.RED + "Something went wrong when processing this command as 'payable'.");
                event.setCancelled(true);
                return;
            }

            double price = payableCommand.getPrice();
            long time = payableCommand.getExpires();
            double discount = configuration.getDiscountPercentage();

            if (!ConfirmCommandManager.isConfirmed(player)) {
                ConfirmCommandManager.requestCommandConfirmation(player, new PayableCommand(
                        command,
                        new ArrayList<>(),
                        price,
                        time,
                        discount
                        )
                );
                event.setCancelled(true);
            }

        }

    }

}
