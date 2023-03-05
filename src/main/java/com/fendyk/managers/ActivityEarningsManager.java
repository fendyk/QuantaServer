package com.fendyk.managers;

import com.fendyk.configs.EarningsConfig;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class ActivityEarningsManager {

    public static EarningsConfig earningsConfig;

    // We will never be able to have more than total supply
    public final static int maxWorkers = 3;

    /**
     * Sets the new kill earnings
     * @param citizenAmount amount of citizens the person has
     * @return the new earnings
     */
    public static double getEarningsFromPvp(int dailyKills, int workers) {
        final double reward = earningsConfig.getPlayerKillEarnings();
        final double threshold = earningsConfig.getPlayerKillThreshold();
        final int citizens = Math.min(workers, maxWorkers);

        double root = Math.max(threshold - Math.min(1, dailyKills + 1), 1);
        double newEarnings = (Math.sqrt(root) * reward) * citizens;
        double oldEarnings = (Math.sqrt(root + 1) * reward) * citizens;
        return oldEarnings - newEarnings;
    }

    /**
     * Sets the new time earnings
     * @param secondsPlayed Amount of seconds played
     * @param citizenAmount amount of citizens the person has
     * @return the new earnings
     */
    public static double getEarningsFromTime(long secondsPlayed, long dailyTimePlayed, int workers) {
        final double reward = earningsConfig.getTimeEarnings();
        final double threshold = earningsConfig.getTimeThreshold();
        final int citizens = Math.min(workers, maxWorkers);

        double root = Math.max(threshold - Math.min(1, dailyTimePlayed + secondsPlayed), secondsPlayed);
        double newEarnings = (Math.sqrt(root) * reward) * citizens;
        double oldEarnings = (Math.sqrt(root + secondsPlayed) * reward) * citizens;
        return oldEarnings - newEarnings;
    }

    /**
     * Sets the new ore earnings
     * @param ore Which ore?
     * @param citizenAmount amount of citizens the person has
     * @return the new earnings
     */
    public static double getEarningsFromMining(Material ore, int dailyOreMined, int workers) {
        final double reward = earningsConfig.getMaterialEarnings(ore);
        final double threshold = earningsConfig.getMaterialThreshold(ore);
        final int citizens = Math.min(workers, maxWorkers);

        double root = Math.max(threshold - Math.min(1, dailyOreMined + 1), 1);
        double newEarnings = (Math.sqrt(root) * reward) * citizens;
        double oldEarnings = (Math.sqrt(root + 1) * reward) * citizens;
        return oldEarnings - newEarnings;
    }

    /**
     * Sets the new pve earnings
     * @param type Which entity?
     * @param citizenAmount amount of citizens the person has
     * @return the new earnings
     */
    public static double getEarningsFromPve(EntityType type, int dailyPveKills, int workers) {
        final double reward = earningsConfig.getEntityEarnings(type);
        final double threshold = earningsConfig.getEntityThreshold(type);
        final int citizens = Math.min(workers, maxWorkers);

        double root = Math.max(threshold - Math.min(1, dailyPveKills + 1), 1);
        double newEarnings = (Math.sqrt(root) * reward) * citizens;
        double oldEarnings = (Math.sqrt(root + 1) * reward) * citizens;
        return oldEarnings - newEarnings;
    }

}
