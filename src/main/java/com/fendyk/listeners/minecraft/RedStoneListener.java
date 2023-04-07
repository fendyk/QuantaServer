package com.fendyk.listeners.minecraft;

import com.fendyk.Main;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class RedStoneListener implements Listener {

    Main main = Main.getInstance();

    @EventHandler
    public void onBlockRedstoneEvent(BlockRedstoneEvent event) {
        event.setNewCurrent(0);
    }


}
