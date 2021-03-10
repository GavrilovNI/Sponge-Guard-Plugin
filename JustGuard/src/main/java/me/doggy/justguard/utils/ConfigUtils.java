package me.doggy.justguard.utils;

import ninja.leaping.configurate.ConfigurationNode;

public class ConfigUtils {

    public static Object getNodeKey(ConfigurationNode node)
    {
        Object[] path = node.getPath();
        if(path.length==0)
            return null;
        return path[path.length-1];
    }
}
