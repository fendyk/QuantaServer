package com.fendyk.managers;

import com.fendyk.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

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

    public static boolean isConfirmed(Player player) {
        return unconfirmedStates.entrySet().stream().anyMatch(
                item -> item.getKey().equals(player.getUniqueId()) && item.getValue()
        );
    }

    public static void requestCommandConfirmation(Player player, String command, double quanta, long timeInSeconds) {
        UUID uuid = player.getUniqueId();
        unconfirmedStates.put(uuid, false);
        unconfirmedCommands.put(uuid, command);
        unconfirmedExpiresInSeconds.put(uuid, timeInSeconds);

        player.sendMessage("You are required to confirm the command before proceeding");
        player.sendMessage("");
        player.sendMessage("Command: " + command);
        player.sendMessage("Cost:" + quanta + " $QTA");
        player.sendMessage("Time left:" + quanta + " $QTA");
        player.sendMessage("");
        player.sendMessage("To confirm your purchase, type /confirm");
    }

    public static void confirmedCommand(Player player) {
        UUID uuid = player.getUniqueId();
        if(!unconfirmedCommands.containsKey(uuid)) {
            player.sendMessage("It looks like you have not confirmed your command in time and has been expired.");
            return;
        }
        unconfirmedStates.put(uuid, true);
        player.performCommand(unconfirmedCommands.get(uuid));
    }

}
