package com.fendyk.clients.fetch;

import com.fendyk.DTOs.MinecraftUserDTO;
import com.fendyk.DTOs.updates.UpdateMinecraftUserDTO;
import com.fendyk.Main;
import com.fendyk.clients.FetchAPI;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.util.UUID;

public class FetchMinecraftUser extends FetchAPI<MinecraftUserDTO, UpdateMinecraftUserDTO> {
    public FetchMinecraftUser(String url) {
        super(url, MinecraftUserDTO.class);
    }
}
