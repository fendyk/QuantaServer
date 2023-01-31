package com.fendyk;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.plugin.messaging.PluginMessageRecipient;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public class ChannelAPI {

    public Request request;

    QuantaServer server;

    public ChannelAPI(QuantaServer server) {
        this.server = server;
        this.request = new Request();
    }

    /**
     * Sends a message through the main channel to a receiver
     * @param event
     * @param data
     */
    private void sendChannelMessage(String event, JsonElement data){
        // Write whatever you want to send to a buffer
        ByteArrayDataOutput buf = ByteStreams.newDataOutput();
        JsonObject newData = new JsonObject();
        newData.addProperty("event", event);
        newData.add("data", data);
        buf.writeUTF(newData.toString());
        // Send it
        server.getServer().sendPluginMessage(server, "quanta:main", buf.toByteArray());
    }

    public class Request {

        /**
         * Deposits x amount to the player
         * @param amount
         */
        public void depositBalance(UUID player, BigDecimal amount) {
            JsonObject json = new JsonObject();
            json.addProperty("uuid", player.toString());
            json.addProperty("amount", amount);
            sendChannelMessage("server:balance:deposit", json);
        }

        /**
         * Deposits x amount to the player
         * @param player
         */
        public void playerBalance(UUID player) {
            JsonObject json = new JsonObject();
            json.addProperty("uuid", player.toString());
            sendChannelMessage("server:balance:deposit", json);
        }


        /**
         * Deposits x amount to the player
         * @param amount
         */
        public void withdrawBalance(UUID player, BigDecimal amount) {
            JsonObject json = new JsonObject();
            json.addProperty("uuid", player.toString());
            json.addProperty("amount", amount);
            sendChannelMessage("server:balance:withdraw", json);
        }

    }

}
