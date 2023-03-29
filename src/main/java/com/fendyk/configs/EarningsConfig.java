package com.fendyk.configs;

import com.fendyk.utilities.Log;
import de.leonhard.storage.Toml;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class EarningsConfig {

    Toml config;
    final double timeEarnings;
    final double timeThreshold;
    final double pvpEarnings;
    final double pvpThreshold;
    HashMap<EntityType, Double> entityEarnings = new HashMap<>();
    HashMap<EntityType, Double> entityThreshold = new HashMap<>();
    HashMap<Material, Double> materialEarnings = new HashMap<>();
    HashMap<Material, Double> materialThreshold = new HashMap<>();


    public EarningsConfig() {
        config = new Toml("earnings", "plugins/QuantaServer");

        timeEarnings = config.getOrSetDefault("timeEarnings", .5);
        timeThreshold = config.getOrSetDefault("timeThreshold", 1);

        pvpEarnings = config.getOrSetDefault("pvpEarnings", 3);
        pvpThreshold = config.getOrSetDefault("pvpThreshold", 2.5);

        if(config.get("materials") == null) {
            // Make default
            config.setDefault("materials.COPPER_ORE.earnings", 0.05D);
            config.setDefault("materials.COPPER_ORE.threshold", 2D);
            config.setDefault("materials.IRON_ORE.earnings", 0.05D);
            config.setDefault("materials.IRON_ORE.threshold", 2D);
        }

        if(config.get("entities") == null) {
            // Make default
            config.setDefault("entities.ZOMBIE.earnings", 0.05D);
            config.setDefault("entities.ZOMBIE.threshold", 2D);
            config.setDefault("entities.SKELETON.earnings", 0.05D);
            config.setDefault("entities.SKELETON.threshold", 2D);
        }

        config.getMap("materials").forEach((name, data) -> {
            Log.info("Adding Ore '" + name.toString() + "' to the server.");
            double earnings = config.getDouble("materials." + name +  ".earnings");
            double threshold = config.getDouble("materials." + name +  ".threshold");
            Bukkit.getLogger().info("Earnings set to: " + earnings);
            Bukkit.getLogger().info("threshold set to: " + threshold);

            materialEarnings.put(Material.getMaterial(name.toString()), earnings);
            materialThreshold.put(Material.getMaterial(name.toString()), threshold);
        });

        config.getMap("entities").forEach((name, data) -> {
            Log.info("Adding Entity '" + name.toString() + "' to the server.");
            double earnings = config.getDouble("entities." + name +  ".earnings");
            double threshold = config.getDouble("entities." + name +  ".threshold");
            Bukkit.getLogger().info("Earnings set to: " + earnings);
            Bukkit.getLogger().info("threshold set to: " + threshold);

            entityEarnings.put(EntityType.valueOf(name.toString()), earnings);
            entityThreshold.put(EntityType.valueOf(name.toString()), threshold);
        });
    }

    public double getTimeEarnings() {
        return timeEarnings;
    }

    public double getTimeThreshold() {
        return timeThreshold;
    }

    public double getPvpEarnings() {
        return pvpEarnings;
    }

    public double getPvpThreshold() {
        return pvpThreshold;
    }

    public HashMap<EntityType, Double> getEntityEarnings() {
        return entityEarnings;
    }

    public HashMap<EntityType, Double> getEntityThreshold() {
        return entityThreshold;
    }

    public HashMap<Material, Double> getMaterialEarnings() {
        return materialEarnings;
    }

    public HashMap<Material, Double> getMaterialThreshold() {
        return materialThreshold;
    }
}
