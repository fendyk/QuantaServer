package com.fendyk.utilities;

import com.fendyk.Main;
import com.fendyk.configs.EarningsConfig;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class ActivityEarnings {

    // We will never be able to have more than total supply
    public final static int maxWorkers = 3;

    /**
     * Sets the new kill earnings
     * @param workers amount of citizens the person has
     * @return the new earnings
     */
    public static double getEarningsFromPvp(int dailyKills, int workers) {
        final EarningsConfig config = Main.getInstance().getEarningsConfig();
        final double reward = config.getPvpEarnings();
        final double threshold = config.getPvpThreshold();
        final int citizens = Math.min(workers, maxWorkers);

        dailyKills = Math.max(dailyKills, 1); // ensure dailyOreMined is at least 1

        // If we reached the threshold, return the maximum earnings.
        if (dailyKills >= threshold) {
            return reward * citizens;
        }

        return reward * citizens * (Math.exp((dailyKills / threshold) * Math.log(2)) - 1);
    }

    /**
     * Sets the new time earnings
     * @param secondsPlayed Amount of seconds played
     * @param workers amount of citizens the person has
     * @return the new earnings
     */
    public static double getEarningsFromTime(long secondsPlayed, long dailyTimePlayed, int workers) {
        final EarningsConfig config = Main.getInstance().getEarningsConfig();
        final double reward = config.getTimeEarnings();
        final double threshold = config.getTimeThreshold();
        final int citizens = Math.min(workers, maxWorkers);

        secondsPlayed = Math.max(secondsPlayed, 1); // ensure dailyOreMined is at least 1

        // If we reached the threshold, return the maximum earnings.
        if (secondsPlayed >= threshold) {
            return reward * citizens;
        }

        return reward * citizens * (Math.exp((secondsPlayed / threshold) * Math.log(2)) - 1);
    }

    /**
     * Sets the new ore earnings
     * @param ore Which ore?
     * @param dailyOreMined How many ores did the player previously mined?
     * @param workers amount of citizens the person has
     * @return the new earnings
     */
    public static double getEarningsFromMining(Material ore, int dailyOreMined, int workers) {
        final EarningsConfig config = Main.getInstance().getEarningsConfig();
        final double reward = config.getMaterialEarnings().get(ore);
        final double threshold = config.getMaterialThreshold().get(ore);
        final int citizens = Math.min(workers, maxWorkers);

        dailyOreMined = Math.max(dailyOreMined, 1); // ensure dailyOreMined is at least 1

        // If we reached the threshold, return the maximum earnings.
        if (dailyOreMined >= threshold) {
            return reward * citizens;
        }

        return reward * citizens * (Math.exp((dailyOreMined / threshold) * Math.log(2)) - 1);
    }

    /**
     * Sets the new pve earnings
     * @param type Which entity?
     * @param workers amount of citizens the person has
     * @return the new earnings
     */
    public static double getEarningsFromPve(EntityType type, int dailyPveKills, int workers) {
        final EarningsConfig config = Main.getInstance().getEarningsConfig();
        final double reward = config.getEntityEarnings().get(type);
        final double threshold = config.getEntityThreshold().get(type);
        final int citizens = Math.min(workers, maxWorkers);

        dailyPveKills = Math.max(dailyPveKills, 1); // ensure dailyOreMined is at least 1

        // If we reached the threshold, return the maximum earnings.
        if (dailyPveKills >= threshold) {
            return reward * citizens;
        }

        return reward * citizens * (Math.exp((dailyPveKills / threshold) * Math.log(2)) - 1);
    }

}
