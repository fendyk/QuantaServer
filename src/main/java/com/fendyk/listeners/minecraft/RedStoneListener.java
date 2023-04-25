package com.fendyk.listeners.minecraft;

import com.fendyk.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class RedStoneListener implements Listener {

    Main main = Main.instance;

    @EventHandler
    public void onBlockRedstoneEvent(BlockRedstoneEvent event) {
        event.setNewCurrent(0);
    }


}
