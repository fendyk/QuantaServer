package com.fendyk.commands;

import com.fendyk.Main;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.FloatArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.ServerOperator;

public class GeneralCommands {
    Main main = Main.instance;
    public GeneralCommands() {
        new CommandAPICommand("speed")
                .withRequirement(ServerOperator::isOp)
                .withArguments(new FloatArgument("amount"))
                .executesPlayer((player, args) -> {
                    player.setFlySpeed((Float) args[0] / 10);
                })
                .register();

        new CommandAPICommand("gm")
                .withRequirement(ServerOperator::isOp)
                .withArguments(new IntegerArgument("number"))
                .executesPlayer((player, args) -> {
                    switch ((Integer) args[0]) {
                        case 0 -> player.setGameMode(GameMode.SURVIVAL);
                        case 1 -> player.setGameMode(GameMode.CREATIVE);
                        case 2 -> player.setGameMode(GameMode.ADVENTURE);
                        case 3 -> player.setGameMode(GameMode.SPECTATOR);
                    }
                })
                .register();

        new CommandAPICommand("tp")
                .withRequirement(ServerOperator::isOp)
                .withArguments(new PlayerArgument("player"))
                .executesPlayer((player, args) -> {
                    player.teleport((Player)args[0]);
                })
                .register();

        new CommandAPICommand("invsee")
                .withRequirement(ServerOperator::isOp)
                .withArguments(new PlayerArgument("player"))
                .executesPlayer((player, args) -> {
                    Player target = (Player) args[0];
                    PlayerInventory targetInventory = target.getInventory();
                    int inventorySize = 54;
                    TextComponent component = Component.text(target.getName() + "'s Inventory");
                    Inventory virtualInventory = Bukkit.createInventory(null, inventorySize, component);

                    // Add target's main inventory (excluding armor and offhand)
                    ItemStack[] contents = targetInventory.getContents();
                    for (int i = 0; i < 36; i++) {
                        virtualInventory.setItem(i, contents[i]);
                    }

                    // Add target's armor and offhand
                    virtualInventory.setItem(45, targetInventory.getHelmet());
                    virtualInventory.setItem(46, targetInventory.getChestplate());
                    virtualInventory.setItem(47, targetInventory.getLeggings());
                    virtualInventory.setItem(48, targetInventory.getBoots());
                    virtualInventory.setItem(50, targetInventory.getItemInOffHand());

                    player.openInventory(virtualInventory);
                })
                .register();
    }

}
