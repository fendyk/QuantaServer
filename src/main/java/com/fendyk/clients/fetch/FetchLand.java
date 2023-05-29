package com.fendyk.clients.fetch;

import com.fendyk.DTOs.LandDTO;
import com.fendyk.DTOs.MinecraftUserDTO;
import com.fendyk.DTOs.updates.UpdateLandDTO;
import com.fendyk.Main;
import com.fendyk.clients.FetchAPI;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.bukkit.Bukkit;

import java.util.UUID;

public class FetchLand extends FetchAPI<LandDTO, UpdateLandDTO> {
    public FetchLand(String url) {
        super(url, LandDTO.class);
    }
}