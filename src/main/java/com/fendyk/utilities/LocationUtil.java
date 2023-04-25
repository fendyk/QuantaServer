package com.fendyk.utilities;

import org.bukkit.Location;

public class LocationUtil {

    /**
     * Verify if the target location is within the radius
     *
     * @param center is where the radius will be based on
     * @param target is the actual location
     * @param radius is the size (in blocks)
     * @return
     */
    public static boolean isWithinRadius(Location center, Location target, int radius) {
        double targetX = target.getX();
        double targetZ = target.getZ();

        return (targetX >= center.getX() - radius) && (targetX <= center.getX() + radius) &&
                (targetZ >= center.getZ() - radius) && (targetZ <= center.getZ() + radius);
    }

}
