package com.fendyk.utilities.extentions;

import com.fendyk.Main;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import net.luckperms.api.model.user.User;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardExtension {

    static Main main = Main.getInstance();

    public static boolean hasBarbarianPermissionToBuildAtGlobalLocation(Player player, Location location) {
        User user = main.getLuckPermsApi().getUserManager().getUser(player.getUniqueId());
        // If the current user is either barbarian or default, verify the flag.
        if(user != null && (user.getPrimaryGroup().equalsIgnoreCase("barbarian") || user.getPrimaryGroup().equalsIgnoreCase("default"))) {
            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            com.sk89q.worldedit.util.Location location1 = BukkitAdapter.adapt(location);

            StateFlag.State state = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(location1, localPlayer, Main.BARBARIAN_BUILD);
            return state != StateFlag.State.DENY;
        }

        return true;
    }

}
