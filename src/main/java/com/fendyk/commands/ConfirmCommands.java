package com.fendyk.commands;

import com.fendyk.Main;
import com.fendyk.managers.ConfirmCommandManager;
import dev.jorel.commandapi.CommandAPICommand;

public class ConfirmCommands {

    public ConfirmCommands() {
        new CommandAPICommand("confirm")
                .executesPlayer((player, args) -> {
                    ConfirmCommandManager.confirmedCommand(player);
                }).register();
    }

}
