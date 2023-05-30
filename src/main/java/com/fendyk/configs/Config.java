package com.fendyk.configs;

import de.leonhard.storage.Toml;
import org.bukkit.Bukkit;
import org.bukkit.World;

public abstract class Config {

    protected Toml config;

    public Config(String name) {
        config = new Toml(name, "plugins/QuantaServer");

        initialize();
    }

    public abstract void initialize();

    public Toml getConfig() {
        return config;
    }
}
