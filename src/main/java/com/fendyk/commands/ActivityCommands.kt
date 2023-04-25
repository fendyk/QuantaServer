package com.fendyk.commands

import com.fendyk.DTOs.ActivitiesDTO
import com.fendyk.Main
import com.fendyk.utilities.Log
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

class ActivityCommands {

    val main: Main = Main.instance;

    /*
    init {
        CommandAPICommand("activities")
            .withAliases("activity")
            .withSubcommand(
                CommandAPICommand("time")
                    .executesPlayer(PlayerCommandExecutor executes@{ sender: CommandSender, args: Array<Any?>? ->
                        val player = sender as Player
                        //val activitiesDTO: ActivitiesDTO? = main.api.activitiesAPI.redis.get(player.uniqueId)

                        val futureActivities: CompletableFuture<ActivitiesDTO?> = main.api.activitiesAPI.get(player)

                        futureActivities.handleAsync { t, u ->
                            val activitiesDTO = futureActivities.join()
                            val timeActivity: ActivitiesDTO? = activitiesDTO.time;

                            player.sendMessage("Name: " + activityDTO.name)
                            player.sendMessage("Earned: " + activityDTO.earnings)
                            player.sendMessage("Amount: " + activityDTO.quantity)
                            player.sendMessage("")
                        }

                        if (activitiesDTO == null) {
                            player.sendMessage("Could not find TIME activity.")
                            return@executes
                        }
                        val activityDTO = activitiesDTO.time
                        player.sendMessage("Name: " + activityDTO!!.name)
                        player.sendMessage("Earned: " + activityDTO.earnings)
                        player.sendMessage("Amount: " + activityDTO.quantity)
                        player.sendMessage("")
                    })
            )
            .withSubcommand(
                CommandAPICommand("pve")
                    .executes(CommandExecutor { sender: CommandSender, args: Array<Any?>? ->
                        val player = sender as Player
                        val activitiesDTO: ActivitiesDTO = main.api.activitiesAPI.redis.get(player.uniqueId).get()
                        if (activitiesDTO == null || activitiesDTO.pve == null || activitiesDTO.pve.size < 1) {
                            player.sendMessage("Could not find PVE activities.")
                            return@executes
                        }
                        for (activityDTO in activitiesDTO.pve) {
                            player.sendMessage("Name: " + activityDTO.name)
                            player.sendMessage("Earned: " + activityDTO.earnings)
                            player.sendMessage("Amount: " + activityDTO.quantity)
                            player.sendMessage("")
                        }
                    })
            )
            .withSubcommand(
                CommandAPICommand("pvp")
                    .executes(CommandExecutor { sender: CommandSender, args: Array<Any?>? ->
                        val player = sender as Player
                        val activitiesDTO: ActivitiesDTO = api.activitiesAPI.redis.get(player.uniqueId)
                        if (activitiesDTO == null || activitiesDTO.pvp == null || activitiesDTO.pvp.size < 1) {
                            player.sendMessage("Could not find PVP activities.")
                            return@executes
                        }
                        for (activityDTO in activitiesDTO.pvp) {
                            player.sendMessage("Name: " + activityDTO.name)
                            player.sendMessage("Earned: " + activityDTO.earnings)
                            player.sendMessage("Amount: " + activityDTO.quantity)
                            player.sendMessage("")
                        }
                    })
            )
            .withSubcommand(
                CommandAPICommand("mining")
                    .executes(CommandExecutor { sender: CommandSender, args: Array<Any?>? ->
                        val player = sender as Player
                        val activitiesDTO: ActivitiesDTO = api.activitiesAPI.redis.get(player.uniqueId)
                        if (activitiesDTO == null || activitiesDTO.mining == null || activitiesDTO.mining.size < 1) {
                            player.sendMessage("Could not find MINING activities.")
                            return@executes
                        }
                        for (activityDTO in activitiesDTO.mining) {
                            player.sendMessage("Name: " + activityDTO.name)
                            player.sendMessage("Earned: " + activityDTO.earnings)
                            player.sendMessage("Amount: " + activityDTO.quantity)
                            player.sendMessage("")
                        }
                    })
            )
            .register()
    }
     */
}