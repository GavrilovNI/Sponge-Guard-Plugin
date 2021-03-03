package me.doggy.justguard.utils;

import ninja.leaping.configurate.ConfigurationNode;

public class ConfigUtils {

    public static Object getNodeKey(ConfigurationNode node)
    {
        Object[] path = node.getPath();
        return path[path.length-1];
    }
}
