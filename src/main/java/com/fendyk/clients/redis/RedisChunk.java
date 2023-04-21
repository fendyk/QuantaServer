package com.fendyk.clients.redis;

import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.clients.RedisAPI;

public class RedisChunk extends RedisAPI<ChunkDTO> {
    public RedisChunk() {
        super("chunk:", ChunkDTO.class);
    }
}
