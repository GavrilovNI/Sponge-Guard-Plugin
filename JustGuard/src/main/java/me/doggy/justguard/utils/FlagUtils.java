package me.doggy.justguard.utils;

import com.google.common.collect.Iterables;
import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.ConfigManager;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.help.RegionPair;
import ninja.leaping.configurate.ConfigurationNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
            if(regionPair.region.getPlayerFlag(player, path).getBoolean(false))
                return false;
        }
        return true;
    }
    public static boolean hasPlayerPermission(Player player, Location<World> location, @NonNull Collection<String> path)
    {
        List<RegionPair> regions = RegionUtils.getHighestPriorityRegions(RegionUtils.getRegionsInLocation(location));

        return hasPlayerPermission(player, regions, path);
    }



    private static boolean containsInGroup(ConfigurationNode groupRoot, String name, String startInh) {

        ConfigurationNode valuesNode = groupRoot.getNode(KEY_VALUES);
        boolean contains = valuesNode.getList(x->x.toString()).contains(name);

        if(contains)
        {
            return true;
        }
        else
        {
            ConfigurationNode inheritsNode = groupRoot.getNode(KEY_INHERITS);

            if(inheritsNode.isVirtual())
                return false;

            String inheritsFromName = inheritsNode.getString();

            if(startInh == null)
                startInh = (String) ConfigUtils.getNodeKey(groupRoot);
            else if (startInh == inheritsFromName)
                return false;

            ConfigurationNode inheritsFrom = groupRoot.getParent().getNode(inheritsFromName);
            return containsInGroup(inheritsFrom, name, startInh);
        }
    }
    public static boolean containsInGroup(ConfigurationNode groupRoot, String name) {
        return containsInGroup(groupRoot, name, null);
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
    public static void makeNodeUseDefault(ConfigurationNode node)
    {
        if(node.isVirtual() || node.isMap())
            return;
        Object value = node.getValue();
        node.getNode(KEY_DEFAULT).setValue(value);
    }


    private static ConfigurationNode getFlag(ConfigurationNode root, @NonNull Iterable<String> path, String startInh) {
        String key = Iterables.getFirst(path, null);

        if(!root.isVirtual() && !root.isMap())
            return root;

        ConfigurationNode result = root.getNode(key);
        Iterable<String> innerPath = Iterables.skip(path, 1);

        if(!result.isVirtual())
        {
            if(Iterables.size(path) == 1)
            {
                return result;
            }
            result = getFlag(result, innerPath, null);
        }

        if(result.isVirtual())
        {
            Map<Object, ConfigurationNode> groups = (Map<Object, ConfigurationNode>) configManager.getGroups().getChildrenMap();

            for(Map.Entry<Object, ConfigurationNode> entry : groups.entrySet()) {
                String groupName = (String) entry.getKey();

                ConfigurationNode groupValueNode = root.getNode(groupName);
                if(groupValueNode.isVirtual() || !containsInGroup(entry.getValue(), key))
                    continue;

                result = groupValueNode;
                break;
            }

            if(result.isVirtual())
                result = root.getNode(KEY_DEFAULT);

            if(!result.isVirtual())
            {
                return result;
            }
            else
            {
                result = root.getNode(KEY_INHERITS);

                if(result.isVirtual())
                    return result;

                String inheritsFromName = result.getString();

                if(startInh == null)
                {
                    startInh = (String) ConfigUtils.getNodeKey(root);
                }
                if (startInh == inheritsFromName)
                {
                    return result;
                }

                ConfigurationNode inheritsFrom = root.getParent().getNode(inheritsFromName);
                return getFlag(inheritsFrom, path, startInh);
            }
        }
        else
        {
            return result;
        }
    }
    public static ConfigurationNode getFlag(ConfigurationNode root, @NonNull Iterable<String> path) {

        ConfigurationNode result = getFlag(root, path, null);
        result = getAllInDefault(result);
        return result;
    }
    public static ConfigurationNode getFlag(ConfigurationNode root, String ... path) {
        return getFlag(root, Arrays.asList(path));
    }

    public static @Nullable Object parseStringToPossibleFlag(String value)
    {
        if(value == null)
            return null;

        String valueLower = value.toLowerCase();

        if(valueLower.equals("true"))
            return true;
        else if(valueLower.equals("false"))
            return false;
        else if(valueLower.equals("null"))
            return null;

        return value;
    }
}
