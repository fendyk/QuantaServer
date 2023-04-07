package com.fendyk.configs;

import com.fendyk.utilities.Log;
import com.fendyk.utilities.RankConfiguration;
import de.leonhard.storage.Toml;

import java.util.ArrayList;
import java.util.Optional;

public class RanksConfig {

    Toml config;
    ArrayList<RankConfiguration> rankConfigurations;

    public RanksConfig() {
        initialize();
    }

    public void initialize() {
        config = new Toml("ranks", "plugins/QuantaServer");
        rankConfigurations = new ArrayList<>();

        if(config.get("ranks") == null) {
            config.setDefault("ranks.default.name", "default");
            config.setDefault("ranks.default.renewableChunkSlots", 1);
            config.setDefault("ranks.default.chunkSlots", 1);
            config.setDefault("ranks.default.memberSlots", 1);
            config.setDefault("ranks.default.discountPercentage", 0);
        }

        config.getMap("ranks").forEach((name, data) -> {
            final String key = "ranks." + name + ".";
            Log.info("Adding registered rank '" + name.toString() + "' to the server.");
            Log.info(data.toString());

            RankConfiguration rankConfiguration = new RankConfiguration(
                    name.toString(),
                    config.getInt(key + "renewableChunkSlots"),
                    config.getInt(key + "chunkSlots"),
                    config.getInt(key + "memberSlots"),
                    config.getDouble(key + "discountPercentage")
            );

            rankConfigurations.add(rankConfiguration);
        });
    }

    public RankConfiguration getRankConfiguration(String name) {
        return rankConfigurations.stream().filter(item -> item.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

}
