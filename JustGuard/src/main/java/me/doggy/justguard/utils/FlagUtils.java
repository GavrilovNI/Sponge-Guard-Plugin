package me.doggy.justguard.utils;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.ConfigManager;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.consts.Texts;
import me.doggy.justguard.flag.FlagPath;
import me.doggy.justguard.utils.help.RegionPair;
import me.doggy.justguard.flag.FlagValue;
import ninja.leaping.configurate.ConfigurationNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

public class FlagUtils {

    private static final Logger logger = JustGuard.getInstance().getLogger();
    private static final ConfigManager configManager = JustGuard.getInstance().getConfigManager();

    public static final String KEY_DEFAULT = "default";
    public static final String KEY_INHERITS = "inherits";
    public static final String KEY_VALUES = "values";

    public static boolean hasPlayerFlagAccess(Player player, List<RegionPair> regions, FlagPath path)
    {
        for (RegionPair regionPair : regions)
        {
            if(!regionPair.region.getPlayerFlag(player, path).getBoolean(false)) {
                return false;
            }
        }
        return true;
    }
    public static boolean hasPlayerFlagAccess(Player player, Location<World> location, FlagPath path)
    {
        List<RegionPair> regions = RegionUtils.getHighestPriorityRegions(RegionUtils.getRegionsInLocation(location));
        return hasPlayerFlagAccess(player, regions, path);
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
    private static FlagValue getFlag(ConfigurationNode root, FlagPath path, @NonNull HashSet<ConfigurationNode> checkedFlags) {

        if(!checkedFlags.add(root))
            return new FlagValue(null);

        if(!root.isMap())
            return new FlagValue(root.getValue());


        String key = path.getFirst();
        ConfigurationNode innerNode = root.getNode(key);

        if(!innerNode.isVirtual()) {
            if(path.length() == 1) {
                return getFlag(innerNode, new FlagPath(KEY_DEFAULT), new HashSet<ConfigurationNode>());
            }

            FlagPath innerPath = path.cut(1);
            FlagValue value = getFlag(innerNode, innerPath, new HashSet<ConfigurationNode>()); // going inside
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

            return new FlagValue(groupValueNode.getValue());
        }

        //looking for default
        ConfigurationNode defaultNode = getAllInDefault(root.getNode(KEY_DEFAULT));
        if(!defaultNode.isVirtual())
            return new FlagValue(defaultNode.getValue());

        //looking in flags this inherits from
        HashSet<ConfigurationNode> inheritsFromNodes = getAllNodesThisInheritsFrom(root, checkedFlags);

        for(ConfigurationNode inheritsFromNode : inheritsFromNodes)
        {
            FlagValue foundValue = getFlag(inheritsFromNode, path, checkedFlags);
            if(!foundValue.isEmpty())
                return foundValue;
        }

        return new FlagValue(null);
    }
    @NonNull
    public static FlagValue getFlag(ConfigurationNode root, FlagPath path) {
        return getFlag(root, path, new HashSet<ConfigurationNode>());
    }
}
