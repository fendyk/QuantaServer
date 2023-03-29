package com.fendyk.managers;

import com.fendyk.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ConfirmCommandManager {

    static Main main = Main.getInstance();

    static HashMap<UUID, Boolean> unconfirmedStates = new HashMap<>();
    static HashMap<UUID, String> unconfirmedCommands = new HashMap<>();
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
            unconfirmedExpiresInSeconds.entrySet().removeIf(entry -> {
                UUID uuid = entry.getKey();
                Long seconds = entry.getValue();

                if(seconds <= 0) {
                    unconfirmedStates.remove(uuid);
                    unconfirmedCommands.remove(uuid);
                    unconfirmedExpiresInSeconds.remove(uuid);
                    return true;
                }
                return false;
            });
        }, 0, 80L);
    }

    /**
     * Checks if the player's command is unchecked
     * @param player
     * @return
     */
    public static boolean isConfirmed(Player player) {
        return unconfirmedStates.entrySet().stream().noneMatch(item -> item.getKey().equals(player.getUniqueId()) && item.getValue());
    }

    public static void requestCommandConfirmation(Player player, String command, double quanta, long timeInSeconds) {
        UUID uuid = player.getUniqueId();
        unconfirmedStates.put(uuid, false);
        unconfirmedCommands.put(uuid, command);
        unconfirmedExpiresInSeconds.put(uuid, timeInSeconds);

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
                        .append(Component.text("/" + command)
                                .color(NamedTextColor.WHITE)
                        )
                )
                .append(Component.newline())
                .append(Component.text("Cost: ")
                        .color(NamedTextColor.GREEN)
                        .append(Component.text(quanta + " $QTA")
                                .color(NamedTextColor.YELLOW)
                        )
                )
                .append(Component.newline())
                .append(Component.text("Time Left: ")
                        .color(NamedTextColor.RED)
                        .append(Component.text(timeInSeconds + " seconds")
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
            if(!unconfirmedCommands.containsKey(uuid)) {
                player.sendMessage("It looks like you have not confirmed your command in time and has been expired.");
                return;
            }
            unconfirmedStates.put(uuid, true);
            player.performCommand(unconfirmedCommands.get(uuid));
            unconfirmedStates.remove(uuid);
            unconfirmedCommands.remove(uuid);
            unconfirmedExpiresInSeconds.remove(uuid);
    }

}
