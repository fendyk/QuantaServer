package com.fendyk.configs;

import de.leonhard.storage.Toml;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.math.BigDecimal;
import java.util.Map;

public class EarningsConfig {

    Toml config;

    public EarningsConfig() {
        config = new Toml("earnings", "plugins/QuantaServer");

        config.setDefault("timeEarnings", .5);
        config.setDefault("timeThreshold", 1);

        config.setDefault("pvpEarnings", 3);
        config.setDefault("pvpThreshold", 2.5);

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

        config.getMap("materials").forEach((name, second) -> {
            Bukkit.getLogger().info("adding ore " + name.toString() + " to the Economy: " + second.toString());
            double earnings = config.getDouble("materials." + name +  ".earnings");
            double threshold = config.getDouble("materials." + name +  ".threshold");
            Bukkit.getLogger().info("Earnings set to: " + earnings);
            Bukkit.getLogger().info("threshold set to: " + threshold);
        });

        config.getMap("entities").forEach((name, second) -> {
            Bukkit.getLogger().info("adding entity " + name.toString() + " to the Economy: " + second.toString());
            double earnings = config.getDouble("entities." + name +  ".earnings");
            double threshold = config.getDouble("entities." + name +  ".threshold");
            Bukkit.getLogger().info("Earnings set to: " + earnings);
            Bukkit.getLogger().info("threshold set to: " + threshold);
        });
    }

    public Map<?, ?> getEntities() {
        return config.getMap("entities");
    }

    public Map<?, ?> getMaterials() {
        return config.getMap("materials");
    }

    /**
     * Returns the material earnings
     * @param entity
     * @return BigDecimal
     */
    public double getEntityEarnings(EntityType entity) {
        return config.getDouble("entities." + entity.name() +  ".earnings");
    }

    /**
     * Returns the material Threshold
     * @param entity
     * @return BigDecimal
     */
    public double getEntityThreshold(EntityType entity) {
        return config.getDouble("entities." + entity.name() +  ".threshold");
    }

    /**
     * Returns the material earnings
     * @param material
     * @return BigDecimal
     */
    public double getMaterialEarnings(Material material) {
        return config.getDouble("materials." + material.name() +  ".earnings");
    }

    /**
     * Returns the material earnings
     * @param material
     * @return BigDecimal
     */
    public double getMaterialThreshold(Material material) {
        return config.getDouble("materials." + material.name() +  ".threshold");
    }

    /**
     * Returns the pvp earnings
     * @return BigDecimal
     */
    public double getPlayerKillEarnings() {
        return config.getDouble("pvpEarnings");
    }

    /**
     * Returns the pvp earnings
     * @return BigDecimal
     */
    public double getPlayerKillThreshold() {
        return config.getDouble("pvpThreshold");
    }

    /**
     * Returns the pvp earnings
     * @return BigDecimal
     */
    public double getTimeThreshold() {
        return config.getDouble("timeThreshold");
    }

    /**
     * Returns the pvp earnings
     * @return BigDecimal
     */
    public double getTimeEarnings() {
        return config.getDouble("timeEarnings");
    }

}
