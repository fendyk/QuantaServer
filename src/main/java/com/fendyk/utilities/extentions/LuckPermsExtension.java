package com.fendyk.utilities.extentions;

import com.fendyk.Main;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.MetaNode;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.Optional;

public class LuckPermsExtension {

    static Main main = Main.instance;
    static LuckPerms luckPerms = main.luckPermsApi;

    /**
     * Get the group with the highest weight from a user
     *
     * @param user
     * @return the rank or 'default' if not found.
     */
    public static String getHighestGroup(Player player) {

        User user = main.luckPermsApi.getPlayerAdapter(Player.class).getUser(player);

        // Find the group with the highest weight
        Optional<InheritanceNode> bestGroupNode = user.getNodes(NodeType.INHERITANCE)
                .stream()
                .max(Comparator.comparingInt(node -> {
                    Group group = luckPerms.getGroupManager().getGroup(node.getGroupName());
                    if (group == null) {
                        return 0;
                    }

                    Optional<MetaNode> weightNode = group.getNodes(NodeType.META).stream()
                            .filter(metaNode -> metaNode.getMetaKey().equalsIgnoreCase("weight"))
                            .findFirst();

                    return weightNode.map(metaNode -> Integer.parseInt(metaNode.getMetaValue())).orElse(0);
                }));

        // If a group is found, return the group name, otherwise return an empty optional
        return bestGroupNode.map(InheritanceNode::getGroupName).orElse("default");
    }

}
