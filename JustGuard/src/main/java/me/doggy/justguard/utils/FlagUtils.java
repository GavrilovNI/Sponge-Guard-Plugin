package me.doggy.justguard.utils;

import com.google.common.collect.Iterables;
import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.ConfigManager;
import me.doggy.justguard.utils.help.RegionPair;
import me.doggy.justguard.utils.help.Flag;
import ninja.leaping.configurate.ConfigurationNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

public class FlagUtils {

    private static final Logger logger = JustGuard.getInstance().getLogger();
    private static final ConfigManager configManager = JustGuard.getInstance().getConfigManager();

    public static final String KEY_DEFAULT = "default";
    public static final String KEY_INHERITS = "inherits";
    public static final String KEY_VALUES = "values";

    public static boolean hasPlayerPermission(Player player, List<RegionPair> regions, @NonNull Collection<String> path)
    {
        for (RegionPair regionPair : regions)
        {
            if(!regionPair.region.getPlayerFlag(player, path).getBoolean(false))
                return false;
        }
        return true;
    }
    public static boolean hasPlayerPermission(Player player, Location<World> location, @NonNull Collection<String> path)
    {
        List<RegionPair> regions = RegionUtils.getHighestPriorityRegions(RegionUtils.getRegionsInLocation(location));

        return hasPlayerPermission(player, regions, path);
    }

    private static HashSet<ConfigurationNode> getAllNodesThisInheritsFrom(ConfigurationNode node, HashSet<ConfigurationNode> alreadyChecked) {
        HashSet<ConfigurationNode> result = new HashSet<>();

        ConfigurationNode parent = node.getParent();
        if(parent == null || parent.isVirtual())
            return result;

        ConfigurationNode inheritNode = node.getNode(KEY_INHERITS);

        if(inheritNode.isVirtual())
            return result;

        HashSet<String> inheritsFromNames = new HashSet<>(inheritNode.getList((x)->(String)x, Arrays.asList(inheritNode.getString())));

        inheritsFromNames.forEach(x -> {
            ConfigurationNode currNode = parent.getNode(x);
            if(!currNode.isVirtual() && !alreadyChecked.contains(currNode))
                result.add(currNode);
        });

        return result;
    }

    private static boolean containsInGroup(ConfigurationNode groupRoot, String name, HashSet<ConfigurationNode> checkedGroups) {

        if(!checkedGroups.add(groupRoot))
           return false;

        ConfigurationNode valuesNode = groupRoot.getNode(KEY_VALUES);

        if(!valuesNode.isVirtual() && valuesNode.getList(x->x.toString()).contains(name))
            return true;

        HashSet<ConfigurationNode> inheritsFromNodes = getAllNodesThisInheritsFrom(groupRoot, checkedGroups);

        for(ConfigurationNode inheritsFromNode : inheritsFromNodes)
        {
            if(containsInGroup(inheritsFromNode, name, checkedGroups))
                return true;
        }

        return false;
    }
    public static boolean containsInGroup(ConfigurationNode groupRoot, String name) {
        return containsInGroup(groupRoot, name, new HashSet<ConfigurationNode>());
    }

    public static ConfigurationNode getAllIn(ConfigurationNode root, String key) {
        ConfigurationNode oldRoot = root;
        while (root.isMap())
            root = root.getNode(key);
        if(root.isVirtual())
            return oldRoot;
        return root;
    }
    public static ConfigurationNode getAllInDefault(ConfigurationNode node) {
        return getAllIn(node, KEY_DEFAULT);
    }
    public static void makeNodeUseDefault(ConfigurationNode node) {
        if(node.isVirtual() || node.isMap())
            return;
        Object value = node.getValue();
        node.getNode(KEY_DEFAULT).setValue(value);
    }


    // return null if not found
    @NonNull
    private static Flag getFlag(ConfigurationNode root, @NonNull Iterable<String> path, @NonNull HashSet<ConfigurationNode> checkedFlags) {

        if(!checkedFlags.add(root))
            return Flag.INSTANCE_EMPTY;

        if(!root.isMap())
            return new Flag(root.getValue());

        String key = Iterables.getFirst(path, null);
        Iterable<String> innerPath = Iterables.skip(path, 1);

        ConfigurationNode innerNode = root.getNode(key);

        if(!innerNode.isVirtual()) {
            if(Iterables.size(path) == 1) {
                return getFlag(innerNode, Arrays.asList(KEY_DEFAULT), new HashSet<ConfigurationNode>());
            }
            Flag value = getFlag(innerNode, innerPath, new HashSet<ConfigurationNode>()); // going inside
            if(!value.isEmpty())
                return value;
        }

        // looking for in groups
        Map<Object, ConfigurationNode> groups = (Map<Object, ConfigurationNode>) configManager.getGroups().getChildrenMap();
        for (Map.Entry<Object, ConfigurationNode> entry : groups.entrySet()) {
            String groupName = (String) entry.getKey();

            ConfigurationNode groupValueNode = root.getNode(groupName);
            if (groupValueNode.isVirtual() || !containsInGroup(entry.getValue(), key))
                continue;

            return new Flag(groupValueNode.getValue());
        }

        //looking for default
        ConfigurationNode defaultNode = getAllInDefault(root.getNode(KEY_DEFAULT));
        if(!defaultNode.isVirtual())
            return new Flag(defaultNode.getValue());

        //looking in flags this inherits from
        HashSet<ConfigurationNode> inheritsFromNodes = getAllNodesThisInheritsFrom(root, checkedFlags);

        for(ConfigurationNode inheritsFromNode : inheritsFromNodes)
        {
            Flag foundValue = getFlag(inheritsFromNode, path, checkedFlags);
            if(!foundValue.isEmpty())
                return foundValue;
        }

        return Flag.INSTANCE_EMPTY;
    }
    @NonNull
    public static Flag getFlag(ConfigurationNode root, @NonNull Iterable<String> path) {
        return getFlag(root, path, new HashSet<ConfigurationNode>());
    }
    @NonNull
    public static Flag getFlag(ConfigurationNode root, String ... path) {
        return getFlag(root, Arrays.asList(path));
    }
}
