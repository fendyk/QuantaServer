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
        config = new Toml("earnings", "plugins/Main");
        config.setDefault("pvp", 2.5);

        if(config.get("materials") == null) {
            // Make default
            config.setDefault("materials.COPPER_ORE.earnings", 0.05D);
            config.setDefault("materials.IRON_ORE.earnings", 0.05D);
        }

        if(config.get("entities") == null) {
            // Make default
            config.setDefault("entities.ZOMBIE.earnings", 0.05D);
            config.setDefault("entities.SKELETON.earnings", 0.05D);
        }

        config.getMap("materials").forEach((name, second) -> {
            Bukkit.getLogger().info("adding ore " + name.toString() + " to the Economy: " + second.toString());
            double earnings = (double) config.get("materials." + name +  ".earnings");
            Bukkit.getLogger().info("Earnings set to: " + earnings);
        });

        config.getMap("entities").forEach((name, second) -> {
            Bukkit.getLogger().info("adding entity " + name.toString() + " to the Economy: " + second.toString());
            double earnings = (double) config.get("entities." + name +  ".earnings");
            Bukkit.getLogger().info("Earnings set to: " + earnings);
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
    public BigDecimal getEntityEarnings(EntityType entity) {
        double earnings = (double) config.get("entities." + entity.name() +  ".earnings");
        return new BigDecimal(earnings);
    }

    /**
     * Returns the material earnings
     * @param material
     * @return BigDecimal
     */
    public BigDecimal getMaterialEarnings(Material material) {
        double earnings = (double) config.get("materials." + material.name() +  ".earnings");
        return new BigDecimal(earnings);
    }

    /**
     * Returns the pvp earnings
     * @return BigDecimal
     */
    public BigDecimal getPlayerKillEarnings() {
        double earnings = config.getDouble("pvp");
        return new BigDecimal(earnings);
    }

}
