package com.fendyk.clients.fetch;

import com.fendyk.DTOs.LandDTO;
import com.fendyk.DTOs.updates.UpdateLandDTO;
import com.fendyk.Main;
import com.fendyk.clients.FetchAPI;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FetchLand extends FetchAPI<String, LandDTO, UpdateLandDTO> {

    public FetchLand() {
        super(LandDTO.class);
    }

    @Override
    public CompletableFuture<LandDTO> get(String key) {
        return fetch("/lands/" + key, RequestMethod.GET, null);
    }

    @Override
    public CompletableFuture<LandDTO> create(LandDTO data) {
        return fetch("/lands", RequestMethod.POST, data);
    }

    @Override
    public CompletableFuture<LandDTO> update(String key, UpdateLandDTO data) {
        return fetch("/lands/" + key, RequestMethod.PATCH, data);
    }

    @Override
    public CompletableFuture<LandDTO> delete(String key) {
        return null;
    }
}