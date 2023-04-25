package com.fendyk.commands

import com.fendyk.Main
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.DoubleArgument
import dev.jorel.commandapi.arguments.OfflinePlayerArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

class EconomyCommands {

    var main = Main.instance

    init {
        CommandAPICommand("pay")
                .withArguments(OfflinePlayerArgument("player"))
                .withArguments(DoubleArgument("amount"))
                .executesPlayer(PlayerCommandExecutor { sender: Player, args: Array<Any> ->
                    val toPlayer: OfflinePlayer = args[0] as OfflinePlayer
                    val amount = args[1] as Double

                    val futWithdrawBalance = main.api.minecraftUserAPI.withDrawBalance(sender, amount)
                    val futDepositBalance = main.api.minecraftUserAPI.depositBalance(toPlayer, amount)

                    CompletableFuture.allOf(futWithdrawBalance, futDepositBalance).handleAsync { t, u ->
                        if (u != null) {
                            sender.sendMessage(u.message.toString())
                        }

                        sender.sendMessage("Your have payed " + amount + " to " + toPlayer.name)

                        if (toPlayer.isOnline && toPlayer.player != null) {
                            toPlayer.player!!.sendMessage("Your have received " + amount + " from " + sender.name)
                        }
                    }
                })
                .register()
    }
}
