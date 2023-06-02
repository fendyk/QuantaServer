package com.fendyk.clients.apis;

import com.fendyk.DTOs.TeleportDTO;
import com.fendyk.clients.ClientAPI;
import com.fendyk.clients.redis.RedisTeleport;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class TeleportAPI extends ClientAPI<Void, RedisTeleport, String, Void> {

    public TeleportAPI(RedisTeleport redis) {
        super(null, redis);
    }

    /**
     * Creates a teleportation request to the player
     * @param from
     * @param to
     */
    public boolean createRequestAsSender(Player sender, Player receiver) {
        DateTime expireDate = new DateTime();
        expireDate.plusMinutes(1);

        TeleportDTO dto = new TeleportDTO();
        dto.setPlayerName(sender.getName());
        dto.setTargetName(receiver.getName());
        dto.setAccepted(false);

        CompletableFuture<Boolean> aFuture = redis.setEx(sender.getName() + ":" + receiver.getName(), dto, 60);
        return aFuture.join();
    }

    /**
     * Accepts a request from the player
     * @param from
     * @param to
     */

    public boolean acceptRequestAsReceiver(Player sender, Player receiver) {
        CompletableFuture<Boolean> aFuture = redis.del(sender.getName() + ":" + receiver.getName());
        return aFuture.join();
    }

    /**
     * Accepts a request from the player
     * @param from
     * @param to
     */

    public TeleportDTO getRequest(Player sender, Player receiver) {
        CompletableFuture<TeleportDTO> aFuture = redis.get(sender.getName() + ":" + receiver.getName());
        return aFuture.join();
    }
}
