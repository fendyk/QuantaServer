package com.fendyk.utilities.extentions;

import com.fendyk.Main;
import com.fendyk.utilities.Log;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.luckperms.api.model.user.User;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardExtension {

    static Main main = Main.getInstance();

    public static boolean hasPermissionToBuildAtGlobalLocation(Player player, Location location) {
        User user = main.getLuckPermsApi().getUserManager().getUser(player.getUniqueId());

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        com.sk89q.worldedit.util.Location location1 = BukkitAdapter.adapt(location);

        // Check the regular build flag
        StateFlag.State stateBuild = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(location1, localPlayer, Flags.BUILD);

        // Check if the player is a member or owner of the region
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(location1);
        boolean isMemberOrOwner = set.isMemberOfAll(localPlayer) || set.isOwnerOfAll(localPlayer);

        // If the player is a member or owner of the region, check the barbarian-build flag (if they are in the barbarian group)
        if (isMemberOrOwner) {
            Log.info("Is member/owner of land");
            if (user != null && user.getPrimaryGroup().equalsIgnoreCase("barbarian")) {
                Log.info("Is barbarian");
                StateFlag.State stateBarbarianBuild = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(location1, localPlayer, Main.BARBARIAN_BUILD);
                Log.info("allowed build barbarian: " + (stateBarbarianBuild == StateFlag.State.ALLOW));
                return stateBarbarianBuild == StateFlag.State.ALLOW;
            }
            Log.info("You should be able to build (non barbarian)");
            // If the player is not in the barbarian group, they can build
            return true;
        }

        // If the player is not a member or owner of the region, check if the build flag is set to ALLOW
        Log.info("allowed build: " + (stateBuild == StateFlag.State.ALLOW));
        return stateBuild == StateFlag.State.ALLOW;
    }

}
