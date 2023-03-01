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
        config.setDefault("timeEffectiveness", 1);

        config.setDefault("pvpEarnings", 3);
        config.setDefault("pvpEffectiveness", 2.5);

        if(config.get("materials") == null) {
            // Make default
            config.setDefault("materials.COPPER_ORE.earnings", 0.05D);
            config.setDefault("materials.COPPER_ORE.effectiveness", 2D);
            config.setDefault("materials.IRON_ORE.earnings", 0.05D);
            config.setDefault("materials.IRON_ORE.effectiveness", 2D);
        }

        if(config.get("entities") == null) {
            // Make default
            config.setDefault("entities.ZOMBIE.earnings", 0.05D);
            config.setDefault("entities.ZOMBIE.effectiveness", 2D);
            config.setDefault("entities.SKELETON.earnings", 0.05D);
            config.setDefault("entities.SKELETON.effectiveness", 2D);
        }

        config.getMap("materials").forEach((name, second) -> {
            Bukkit.getLogger().info("adding ore " + name.toString() + " to the Economy: " + second.toString());
            double earnings = config.getDouble("materials." + name +  ".earnings");
            double effectiveness = config.getDouble("materials." + name +  ".effectiveness");
            Bukkit.getLogger().info("Earnings set to: " + earnings);
            Bukkit.getLogger().info("Effectiveness set to: " + effectiveness);
        });

        config.getMap("entities").forEach((name, second) -> {
            Bukkit.getLogger().info("adding entity " + name.toString() + " to the Economy: " + second.toString());
            double earnings = config.getDouble("entities." + name +  ".earnings");
            double effectiveness = config.getDouble("entities." + name +  ".effectiveness");
            Bukkit.getLogger().info("Earnings set to: " + earnings);
            Bukkit.getLogger().info("Effectiveness set to: " + effectiveness);
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
     * Returns the material effectiveness
     * @param entity
     * @return BigDecimal
     */
    public double getEntityEffectiveness(EntityType entity) {
        return config.getDouble("entities." + entity.name() +  ".effectiveness");
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
    public double getMaterialEffectiveness(Material material) {
        return config.getDouble("materials." + material.name() +  ".effectiveness");
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
    public double getPlayerKillEffectiveness() {
        return config.getDouble("pvpEffectiveness");
    }

    /**
     * Returns the pvp earnings
     * @return BigDecimal
     */
    public double getTimeEffectiveness() {
        return config.getDouble("timeEffectiveness");
    }

    /**
     * Returns the pvp earnings
     * @return BigDecimal
     */
    public double getTimeEarnings() {
        return config.getDouble("timeEarnings");
    }

}
