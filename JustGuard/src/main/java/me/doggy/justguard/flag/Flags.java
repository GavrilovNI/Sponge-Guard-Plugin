package me.doggy.justguard.flag;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.ConfigManager;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.consts.Texts;
import me.doggy.justguard.utils.ConfigUtils;
import me.doggy.justguard.utils.FlagUtils;
import me.doggy.justguard.utils.MessageUtils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Flags {

    private static final ConfigManager configManager = JustGuard.getInstance().getConfigManager();

    private static final String DEFAULT_KEY = "default";
    private static final String GROUP_PREFIX = "group::";

    private ConfigurationNode root;

    public Flags(ConfigurationNode flagsNode) {
        this.root = flagsNode;
    }
    public Flags(Flags other) {
        this.root = other.root.copy();
    }

    public void save(ConfigurationLoader loader) throws IOException {
        loader.save(root);
    }

    @NonNull
    private static FlagValue getFlag(ConfigurationNode currNode, FlagPath path, @NonNull HashSet<ConfigurationNode> checkedFlags) {

        if(!checkedFlags.add(currNode))
            return new FlagValue(null);

        if(!currNode.isMap())
            return new FlagValue(currNode.getValue());


        String key = path.getFirst();
        ConfigurationNode innerNode = currNode.getNode(key);

        if(!innerNode.isVirtual()) {
            if(path.length() == 1) {
                return getFlag(innerNode, new FlagPath(DEFAULT_KEY), new HashSet<ConfigurationNode>());
            }

            FlagPath innerPath = path.cut(1);
            FlagValue value = getFlag(innerNode, innerPath, new HashSet<ConfigurationNode>()); // going inside
            if(!value.isEmpty())
                return value;
        }

        // looking for in groups
        Groups groups = configManager.getGroups();
        Set<String> groupNames = groups.getGroupNames();
        for (String groupName : groupNames) {
            ConfigurationNode groupValueNode = currNode.getNode(GROUP_PREFIX + groupName);
            if (groupValueNode.isVirtual() || !groups.containsInGroup(groupName, key))
                continue;
            return new FlagValue(groupValueNode.getValue());
        }

        //looking for default
        ConfigurationNode defaultNode = ConfigUtils.getAllInside(currNode.getNode(DEFAULT_KEY), DEFAULT_KEY);
        if(!defaultNode.isVirtual())
            return new FlagValue(defaultNode.getValue());

        //looking in flags this inherits from
        HashSet<ConfigurationNode> inheritsFromNodes = ConfigUtils.getAllNodesThisInheritsFrom(currNode, checkedFlags);

        for(ConfigurationNode inheritsFromNode : inheritsFromNodes)
        {
            FlagValue foundValue = getFlag(inheritsFromNode, path, checkedFlags);
            if(!foundValue.isEmpty())
                return foundValue;
        }

        return new FlagValue(null);
    }
    @NonNull
    public FlagValue getFlag(FlagPath path) {
        return getFlag(root, path, new HashSet<ConfigurationNode>());
    }

    private static void makeNodeUseDefault(ConfigurationNode node) {
        if(node.isVirtual() || node.isMap())
            return;
        Object value = node.getValue();
        node.getNode(DEFAULT_KEY).setValue(value);
    }
    public FlagValue setFlag(FlagPath path, FlagValue value) {

        ConfigurationNode currNode = root;
        for(String currPath : path) {
            Flags.makeNodeUseDefault(currNode);
            currNode = currNode.getNode(currPath);
        }
        currNode = ConfigUtils.getAllInside(currNode, DEFAULT_KEY);

        FlagValue oldValue = new FlagValue(currNode.getValue());
        currNode.setValue(value.getValue());
        return oldValue;
    }
}
