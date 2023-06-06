package com.fendyk.configs;

import de.leonhard.storage.Toml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// TODO: Messages should be expanded
public class MessagesConfig {
    Toml config;

    public enum State {
        CHUNK_IS_BLACKLISTED,
        CHUNK_IS_NOT_CLAIMABLE
    }

    HashMap<State, String> messages = new HashMap<>();

    public MessagesConfig() {
        initialize();
    }

    public void initialize() {
        config = new Toml("messages", "plugins/QuantaServer");

        messages.put(State.CHUNK_IS_BLACKLISTED,
                config.getOrSetDefault(State.CHUNK_IS_BLACKLISTED.toString(),
                        "The chunk is 'blacklisted' and unclimbable.")
        );
        messages.put(State.CHUNK_IS_NOT_CLAIMABLE,
                config.getOrSetDefault(State.CHUNK_IS_NOT_CLAIMABLE.toString(),
                        "The chunk is not considered claimable.")
        );
    }

    public HashMap<State, String> getMessages() {
        return messages;
    }

    public String getMessage(State state) {
        return messages.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(state))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }
}
