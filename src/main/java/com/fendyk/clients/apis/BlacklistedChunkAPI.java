package com.fendyk.clients.apis;

import com.fendyk.API;
import com.fendyk.clients.ClientAPI;
import com.fendyk.clients.redis.RedisBlacklistedChunk;
import com.fendyk.utilities.Vector2;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class BlacklistedChunkAPI extends ClientAPI<Class<?>, RedisBlacklistedChunk> {

    public BlacklistedChunkAPI(API api, Class<?> fetch, RedisBlacklistedChunk redis) {
        super(api, null, redis);
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


}
