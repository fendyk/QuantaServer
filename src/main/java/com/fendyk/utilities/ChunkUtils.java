package com.fendyk.utilities;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class ChunkUtils {

    /**
     * Return an array length of 4 - (topLeft, topRight, bottomLeft, bottomRight)
     * @return
     */
    public static Location[] getAreaCorners(int radius) {
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

    /**
     * Verifies if both chunks are the same by validating their coordinates.
     * @param chunk1
     * @param chunk2
     * @return
     */
    public static boolean isSameChunk(Chunk chunk1, Chunk chunk2) {
        return chunk1.getX() == chunk2.getX() && chunk1.getZ() == chunk2.getZ();
    }

    public static List<Vector2> getChunkCoordsInRadius(int centerX, int centerZ, int radius) {
        List<Vector2> coords = new ArrayList<>();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x*x + z*z <= radius*radius) {
                    coords.add(new Vector2(centerX + x, centerX + z));
                }
            }
        }
        return coords;
    }

    /**
     * Finds out if the chunk is inside the radius of the center
     * @param location
     * @param chunk
     * @param radius
     * @return
     */
    public boolean isChunkInRadius(Location location, Chunk chunk, int radius) {
        World world = location.getWorld();
        int centerX = location.getBlockX() >> 4;
        int centerZ = location.getBlockZ() >> 4;
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        int dx = Math.abs(centerX - chunkX);
        int dz = Math.abs(centerZ - chunkZ);
        return dx <= radius && dz <= radius;
    }

    public static Location getChunkCenter(Chunk chunk) {
        int x = (chunk.getX() << 4) + 8; // calculate the X coordinate of the center of the chunk
        int z = (chunk.getZ() << 4) + 8; // calculate the Z coordinate of the center of the chunk
        int y = chunk.getWorld().getHighestBlockYAt(x, z); // get the highest block Y coordinate at the center of the chunk
        return new Location(chunk.getWorld(), x, y, z); // create and return a new Location object with the center coordinates
    }

    public static List<Chunk> getNeighboringChunks(Chunk chunk) {
        List<Chunk> chunks = new ArrayList<>();
        World world = chunk.getWorld();

        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        // Get the neighboring chunks
        chunks.add(world.getChunkAt(chunkX + 1, chunkZ));
        chunks.add(world.getChunkAt(chunkX - 1, chunkZ));
        chunks.add(world.getChunkAt(chunkX, chunkZ + 1));
        chunks.add(world.getChunkAt(chunkX, chunkZ - 1));
        return chunks;
    }

    public static List<Location> getChunkBounds(Chunk chunk, double height) {
        List<Location> bounds = new ArrayList<Location>();
        int minX = chunk.getX() * 16;
        int minZ = chunk.getZ() * 16;
        int maxX = minX + 15;
        int maxZ = minZ + 15;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                Location location = new Location(chunk.getWorld(), x, height, z);
                // found a block at this location, check if it's at the edge of the chunk
                if (x == minX || x == maxX || z == minZ || z == maxZ) {
                    bounds.add(location);
                }
            }
        }
        return bounds;
    }


}
