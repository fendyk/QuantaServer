package com.fendyk.commands

import com.fendyk.managers.ConfirmCommandManager
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.entity.Player

class ConfirmCommands {
    init {
        CommandAPICommand("confirm")
            .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?>? ->
                ConfirmCommandManager.confirmedCommand(
                    player
                )
            }).register()
    }
}
