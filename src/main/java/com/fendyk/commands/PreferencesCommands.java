package com.fendyk.commands;

import com.fendyk.Main;
import com.fendyk.managers.ConfirmCommandManager;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.permissions.ServerOperator;

import java.math.BigDecimal;

public class PreferencesCommands {

    Main main = Main.getInstance();

    public PreferencesCommands() {
        new CommandAPICommand("main")
                .withRequirement(ServerOperator::isOp)
                .withPermission(CommandPermission.OP)
                .withSubcommand(new CommandAPICommand("reload")
                        .executesPlayer((player, args) -> {
                            main.getServerConfig().initialize();
                            main.getPricesConfig().initialize();
                            main.getRanksConfig().initialize();
                            main.getEarningsConfig().initialize();
                            main.getApi().reconnect();
                            player.sendMessage(ChatColor.GREEN + "Plugin configurations have been reloaded");
                        })
                )
                .register();
    }

}
