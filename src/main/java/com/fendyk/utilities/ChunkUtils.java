package com.fendyk.utilities;

import org.bukkit.Location;

import java.util.ArrayList;

public class ChunkUtils {

    /**
     * Return an array length of 4 - (topLeft, topRight, bottomLeft, bottomRight)
     * @param chunks
     * @return
     */
    public static Location[] getAreaCorners(int radius) {
        int x = 0; // Center chunk x coordinate
        int z = 0; // Center chunk z coordinate
        int blockRadius = radius * 16; // Radius in blocks
        Location[] corners = new Location[4]; // Array to hold the four corners

        // Calculate the coordinates of each corner
        corners[0] = new Location(null, -blockRadius, 0, -blockRadius); // Top left corner
        corners[1] = new Location(null, blockRadius, 0, -blockRadius); // Top right corner
        corners[2] = new Location(null, -blockRadius, 0, blockRadius); // Bottom left corner
        corners[3] = new Location(null, blockRadius, 0, blockRadius); // Bottom right corner

        return corners;
    }

    /**
     * Returns total radius of the chunk count
     * @param chunkCount
     * @return
     */
    public static int getRadiusInChunks(int chunkCount) {
        double radius = Math.sqrt(chunkCount / Math.PI);
        return (int) Math.ceil(radius);
    }

}
