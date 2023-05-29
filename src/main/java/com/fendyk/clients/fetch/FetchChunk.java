package com.fendyk.clients.fetch;

import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.DTOs.LandDTO;
import com.fendyk.DTOs.updates.UpdateChunkDTO;
import com.fendyk.Main;
import com.fendyk.clients.FetchAPI;
import com.fendyk.utilities.Vector2;
import okhttp3.Request;
import okhttp3.RequestBody;

public class FetchChunk extends FetchAPI<ChunkDTO, UpdateChunkDTO> {
    public FetchChunk(String url) {
        super(url, ChunkDTO.class);
    }
}
