package com.fendyk.configs;

import com.fendyk.utilities.Log;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.leonhard.storage.Toml;

import java.util.ArrayList;
import java.util.HashMap;

public class PricesConfig {

    Toml config;
    final double spawnCommandPrice;

    ArrayList<String> commands = new ArrayList<>();
    HashMap<String, Double> commandPrices = new HashMap<>();
    HashMap<String, Double> commandTimeInSeconds = new HashMap<>();

    public PricesConfig() {
        config = new Toml("prices", "plugins/QuantaServer");

        spawnCommandPrice = config.getOrSetDefault("spawnCommandPrice", 1D);

        if(config.get("commands") == null) {
            config.setDefault("commands.spawn.command", "spawn");
            config.setDefault("commands.spawn.price", 1D);
            config.setDefault("commands.spawn.timeInSeconds", 30D);
        }


        config.getMap("commands").forEach((name, data) -> {
            Log.info("Adding registered payed command '" + name.toString() + "' to the server.");
            Log.info(data.toString());
            JsonObject json = JsonParser.parseString(data.toString()).getAsJsonObject();

            String command = json.get("command").getAsString();
            Double price = json.get("price").getAsDouble();
            Long time = json.get("timeInSeconds").getAsLong();
        });

    }

    public double getSpawnCommandPrice() {
        return spawnCommandPrice;
    }

    public boolean payedCommandsExists(String name) {
        return commands.stream().anyMatch(item -> item.equalsIgnoreCase(name));
    }

    public double getPayedCommandPrice(String name) {
        return commandPrices.entrySet().stream().filter(item -> item.getKey().equalsIgnoreCase(name)).findFirst().get().getValue();
    }
}
