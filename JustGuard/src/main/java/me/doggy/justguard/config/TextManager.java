package me.doggy.justguard.config;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.utils.FileUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;

public class TextManager {

    private static ConfigurationNode texts = null;

    public static void load()
    {
        texts = FileUtils.getFileNode("texts.conf", JustGuard.getInstance().getConfigManager().getConfigDir());
    }

    public static String getText(String key)
    {
        ConfigurationNode text = texts.getNode(key);
        if(text.isVirtual())
            return key;

        return text.getString(key);
    }

    public static String getText(String key, @NonNull String... changes)
    {
        String str = getText(key);

        for (int i = 0; i < changes.length; i++)
        {
            str = str.replace('{' + String.valueOf(i) + '}', changes[i]);
        }

        return str;
    }

}
