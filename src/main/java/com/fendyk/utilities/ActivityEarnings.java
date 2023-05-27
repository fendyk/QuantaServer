package com.fendyk.utilities;

import com.fendyk.Main;
import com.fendyk.configs.EarningsConfig;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class ActivityEarnings {

    // We will never be able to have more than total supply
    public final static int MAX_WORKERS = 3;

    /**
     * Sets the new kill earnings
     * @param workers amount of citizens the person has
     * @return the new earnings (always at least 0 or greater)
     */
    public static double getEarningsFromPvp(int dailyKills, int workers) {
        final EarningsConfig config = Main.getInstance().getEarningsConfig();
        final double reward = Math.max(0, config.getPvpEarnings());
        final double threshold = Math.max(0, config.getPvpThreshold());
        final int citizens = Math.min(Math.max(0, workers), MAX_WORKERS);

        dailyKills = Math.max(dailyKills, 1); // ensure dailyOreMined is at least 1

        // If we reached the threshold, return the maximum earnings.
        if (dailyKills >= threshold) {
            return Math.max(0, reward * citizens);
        }

        return Math.max(0, reward * citizens * (Math.exp((dailyKills / threshold) * Math.log(2)) - 1));
    }

    /**
     * Sets the new time earnings
     * @param secondsPlayed Amount of seconds played
     * @param workers amount of citizens the person has
     * @return the new earnings (always at least 0 or greater)
     */
    public static double getEarningsFromTime(long secondsPlayed, long dailyTimePlayed, int workers) {
        final EarningsConfig config = Main.getInstance().getEarningsConfig();
        final double reward = Math.max(0, config.getTimeEarnings());
        final double threshold = Math.max(0, config.getTimeThreshold());
        final int citizens = Math.min(Math.max(0, workers), MAX_WORKERS);

        secondsPlayed = Math.max(secondsPlayed, 1); // ensure dailyOreMined is at least 1

        // If we reached the threshold, return the maximum earnings.
        if (secondsPlayed >= threshold) {
            return Math.max(0, reward * citizens);
        }

        return Math.max(0, reward * citizens * (Math.exp((secondsPlayed / threshold) * Math.log(2)) - 1));
    }

    /**
     * Sets the new ore earnings
     * @param ore Which ore?
     * @param dailyOreMined How many ores did the player previously mined?
     * @param workers amount of citizens the person has
     * @return the new earnings (always at least 0 or greater)
     */
    public static double getEarningsFromMining(Material ore, int dailyOreMined, int workers) {
        final EarningsConfig config = Main.getInstance().getEarningsConfig();
        final double reward = Math.max(0, config.getMaterialEarnings().get(ore));
        final double threshold = Math.max(0, config.getMaterialThreshold().get(ore));
        final int citizens = Math.min(Math.max(0, workers), MAX_WORKERS);

        dailyOreMined = Math.max(dailyOreMined, 1); // ensure dailyOreMined is at least 1

        // If we reached the threshold, return the maximum earnings.
        if (dailyOreMined >= threshold) {
            return Math.max(0, reward * citizens);
        }

        return Math.max(0, reward * citizens * (Math.exp((dailyOreMined / threshold) * Math.log(2)) - 1));
    }

    /**
     * Sets the new pve earnings
     * @param type Which entity?
     * @param workers amount of citizens the person has
     * @return the new earnings (always at least 0 or greater)
     */
    public static double getEarningsFromPve(EntityType type, int dailyPveKills, int workers) {
        final EarningsConfig config = Main.getInstance().getEarningsConfig();
        final double reward = Math.max(0, config.getEntityEarnings().get(type));
        final double threshold = Math.max(0, config.getEntityThreshold().get(type));
        final int citizens = Math.min(Math.max(0, workers), MAX_WORKERS);

        dailyPveKills = Math.max(dailyPveKills, 1); // ensure dailyOreMined is at least 1

        // If we reached the threshold, return the maximum earnings.
        if (dailyPveKills >= threshold) {
            return Math.max(0, reward * citizens);
        }

        // MAke sure number is atleast 0 or greater

        return Math.max(0, reward * citizens * (Math.exp((dailyPveKills / threshold) * Math.log(2)) - 1));
    }

}
