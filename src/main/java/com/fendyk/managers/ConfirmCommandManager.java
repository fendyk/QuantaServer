package com.fendyk.managers;

import com.fendyk.Main;
import com.fendyk.utilities.Log;
import com.fendyk.utilities.PayableCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ConfirmCommandManager {

    static Main main = Main.getInstance();

    static HashMap<UUID, Boolean> unconfirmedStates = new HashMap<>();
    static HashMap<UUID, PayableCommand> unconfirmedPayableCommands = new HashMap<>();
    static HashMap<UUID, Long> unconfirmedExpiresInSeconds = new HashMap<>();

    /**
     * -> Player excecutes command /land create
     * -> Verify if the command is confirmed
     * -> If not, requireConfirmation() gets called
     * -> Send player with a message to confirm the command by
     * either /confirm or clicking
     * -> If the player confirms within the time, execute the command
     * -> Or remove the data from the hashmap
     */

    public static void watch() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(main, () -> {
            unconfirmedExpiresInSeconds.replaceAll((k, v) -> v - 4);

            Iterator<Map.Entry<UUID, Long>> iterator = unconfirmedExpiresInSeconds.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<UUID, Long> entry = iterator.next();
                UUID uuid = entry.getKey();
                Long seconds = entry.getValue();

                if (seconds <= 0) {
                    unconfirmedStates.remove(uuid);
                    unconfirmedPayableCommands.remove(uuid);
                    iterator.remove();
                }
            }
        }, 0, 80L);
    }


    /**
     * Checks if the player's command is unchecked
     * @param player
     * @return
     */
    public static boolean isConfirmed(Player player) {
        return unconfirmedStates.entrySet().stream().anyMatch(item -> item.getKey().equals(player.getUniqueId()) && item.getValue());
    }

    public static void requestCommandConfirmation(Player player, PayableCommand payableCommand) {
        UUID uuid = player.getUniqueId();
        String cmd = payableCommand.getCommand();
        double price = payableCommand.getPrice();
        long expires = payableCommand.getExpires();
        double discountPercentage = payableCommand.getDiscountPercentage();

        unconfirmedStates.put(uuid, false);
        unconfirmedPayableCommands.put(uuid, payableCommand);
        unconfirmedExpiresInSeconds.put(uuid, expires);

        Component message = Component.empty()
                .append(Component.newline())
                .append(Component.text("You are required to confirm the command before proceeding")
                        .color(NamedTextColor.GOLD)
                        .decoration(TextDecoration.BOLD, true)
                )
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("Command: ")
                        .color(NamedTextColor.AQUA)
                        .append(Component.text(cmd)
                                .color(NamedTextColor.WHITE)
                        )
                )
                .append(Component.newline())
                .append(Component.text("Cost: ")
                        .color(NamedTextColor.GREEN)
                        .append(discountPercentage > 0 ?
                                Component.text(String.format("%.2f", price) + " $QTA")
                                        .color(NamedTextColor.YELLOW)
                                        .decoration(TextDecoration.STRIKETHROUGH, true)
                                        .append(Component.space())
                                :
                                Component.empty()
                        )
                        .append(discountPercentage > 0 ?
                                Component.text(" (-" + discountPercentage + "%) " + String.format("%.2f", price * (1 - discountPercentage / 100)) + " $QTA")
                                        .color(NamedTextColor.YELLOW)
                                :
                                Component.text(String.format("%.2f", price) + " $QTA")
                                        .color(NamedTextColor.YELLOW)
                        )
                )
                .append(Component.newline())
                .append(Component.text("Time Left: ")
                        .color(NamedTextColor.RED)
                        .append(Component.text(expires + " seconds")
                                .color(NamedTextColor.YELLOW)
                        )
                )
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("To confirm your purchase, type ")
                        .color(NamedTextColor.AQUA)
                        .append(Component.text("/confirm")
                                .color(NamedTextColor.GOLD)
                                .decoration(TextDecoration.BOLD, true)
                        )
                )
                .append(Component.newline());
        player.sendMessage(message);
    }

    public static void confirmedCommand(Player player) {
        UUID uuid = player.getUniqueId();
        if(!unconfirmedPayableCommands.containsKey(uuid)) {
            player.sendMessage("It looks like you have not confirmed your command in time and has been expired.");
            return;
        }
        PayableCommand payableCommand = unconfirmedPayableCommands.get(uuid);
        String cmd = payableCommand.getCommand();
        double price = payableCommand.getPrice();
        double discountPercentage = payableCommand.getDiscountPercentage();
        double discountPrice = price * (1 - discountPercentage / 100);

        // Verify balance exists
        BigDecimal bal = main.getApi().getMinecraftUserAPI().getPlayerBalance(uuid);
        if(bal == null) {
            player.sendMessage(ChatColor.RED + "Could not find your balance.");
            return;
        }

        // Verify balance is enough
        double balDouble = bal.doubleValue();
        if(balDouble < discountPrice) {
            player.sendMessage(ChatColor.RED + "You have insufficient balance in your account." +
                    " You need at least " + (discountPrice - balDouble) + " $QTA");
            return;
        }

        // Verify if is withdrawn
        boolean isWithdrawn = main.getApi().getMinecraftUserAPI().withDrawBalance(player, new BigDecimal(discountPrice));
        if(!isWithdrawn) {
            player.sendMessage(ChatColor.RED + "Could not withdraw money.");
            return;
        }

        unconfirmedStates.put(uuid, true);

        // Perform command after checks
        boolean isPerformed = player.performCommand(cmd.substring(1));
        if(!isPerformed) return;

        player.sendActionBar(
                Component.text("You've purchased the " + cmd + " command for " + String.format("%.4f", discountPrice) + " $QTA")
                        .color(NamedTextColor.GREEN)
        );

        // Remove states
        unconfirmedStates.remove(uuid);
        unconfirmedPayableCommands.remove(uuid);
        unconfirmedExpiresInSeconds.remove(uuid);
    }

}
