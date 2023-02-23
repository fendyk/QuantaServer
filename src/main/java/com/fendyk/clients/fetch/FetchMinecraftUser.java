package com.fendyk.clients.fetch;

import com.fendyk.DTOs.MinecraftUserDTO;
import com.fendyk.Main;
import com.fendyk.clients.FetchAPI;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.UUID;

public class FetchMinecraftUser extends FetchAPI<UUID, MinecraftUserDTO, MinecraftUserDTO> {

    public FetchMinecraftUser(Main server, String url, boolean inDebugMode) {
        super(server, url, inDebugMode);
    }

    @Override
    public MinecraftUserDTO get(UUID key) {
        Request request = new Request.Builder()
                .url(url + "/minecraftusers/" + key.toString())
                .get()
                .build();
        return Main.gson.fromJson(
                fetchFromApi(request, "fetchMinecraftUser"),
                MinecraftUserDTO.class
        );
    }

    @Override
    public MinecraftUserDTO create(MinecraftUserDTO data) {
        return null;
    }

    @Override
    public MinecraftUserDTO update(UUID key, MinecraftUserDTO data) {
        RequestBody body = RequestBody.create(Main.gson.toJson(data), JSON);
        Request request = new Request.Builder()
                .url(url + "/minecraftusers/" + key)
                .patch(body)
                .build();
        return Main.gson.fromJson(
                fetchFromApi(request, "updateMinecraftUser"),
                MinecraftUserDTO.class
        );
    }

    @Override
    public MinecraftUserDTO delete(UUID any) {
        return null;
    }

}
