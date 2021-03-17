package me.doggy.justguard.config;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.utils.FileUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.util.Arrays;

public class TextManager {

    private static final ConfigManager configManager = JustGuard.getInstance().getConfigManager();
    private static ConfigurationNode texts = null;

    public static void load()
    {
        final String textsFileName = "texts.conf";
        texts = FileUtils.getFileNodeUsingResources(new File(configManager.getConfigDir(), textsFileName), textsFileName);
    }

    public static String getText(String key)
    {
        ConfigurationNode text = texts.getNode(key.toLowerCase());
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
