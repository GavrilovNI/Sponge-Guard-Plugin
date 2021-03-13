package me.doggy.justguard.utils;

import ninja.leaping.configurate.ConfigurationNode;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.HashSet;

public class ConfigUtils {

    private static final String KEY_INHERITS = "inherits";

    public static Object getNodeKey(ConfigurationNode node)
    {
        Object[] path = node.getPath();
        if(path.length==0)
            return null;
        return path[path.length-1];
    }

    public static ConfigurationNode getAllInside(ConfigurationNode root, String key) {
        ConfigurationNode oldRoot = root;
        while (root.isMap())
            root = root.getNode(key);
        if(root.isVirtual())
            return oldRoot;
        return root;
    }

    public static HashSet<ConfigurationNode> getAllNodesThisInheritsFrom(ConfigurationNode node, @NonNull HashSet<ConfigurationNode> alreadyChecked) {
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
    private static HashSet<ConfigurationNode> getAllNodesThisInheritsFrom(ConfigurationNode node) {
        return getAllNodesThisInheritsFrom(node, new HashSet<ConfigurationNode>());
    }
}
