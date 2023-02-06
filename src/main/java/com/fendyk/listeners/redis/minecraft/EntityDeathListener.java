package com.fendyk.listeners.redis.minecraft;

import com.fendyk.QuantaServer;
import com.fendyk.RedisAPI;
import com.fendyk.configs.EarningsConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class EntityDeathListener implements Listener {

    QuantaServer server;

    public EntityDeathListener(QuantaServer server) {
        this.server = server;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) throws IOException {
        if(event.getEntity().getKiller() != null) {

            Entity killed = event.getEntity();
            Entity killer = event.getEntity().getKiller();

            EarningsConfig config = server.getEarningsConfig();
            RedisAPI redisAPI = server.getRedisAPI();

            assert killer != null;

            if(killed instanceof Player) {
                BigDecimal amount  = config.getPlayerKillEarnings().setScale(2, RoundingMode.HALF_EVEN);
                redisAPI.depositBalance(killer.getUniqueId(), amount);
                killer.sendMessage("You have killed " + killed.getName() + " and received " + amount.toString() + "quanta");
            }
            else if(config.getEntities().containsKey(killed.getType().name())) {
                BigDecimal amount  = config.getEntityEarnings(killed.getType()).setScale(2, RoundingMode.HALF_EVEN);
                redisAPI.depositBalance(killer.getUniqueId(), amount);
                killer.sendMessage("You have killed a " + killed.getType() + " and received " + amount + "quanta");
            }
        }
    }

}
