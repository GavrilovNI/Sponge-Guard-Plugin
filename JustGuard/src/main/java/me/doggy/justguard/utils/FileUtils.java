package me.doggy.justguard.utils;

import com.google.common.collect.Iterables;
import com.google.common.reflect.TypeToken;
import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.ConfigManager;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.List;

public class FileUtils {

    private static final Logger logger = JustGuard.getInstance().getLogger();


    public static boolean copyResource(String name, Path target, StandardCopyOption option) {
        ClassLoader classLoader = ConfigManager.class.getClassLoader();
        InputStream stream = classLoader.getResourceAsStream(name);
        if (stream != null)
        {
            try
            {
                Files.copy(stream, target, option);
                return true;
            }
            catch (IOException e)
            {
                logger.error("Error on copying resource '"+name+"'.", e);
            }

        }
        else
        {
            logger.error("Can't find resource '"+name+"'.");
        }
        return false;
    }
    public static ConfigurationNode getFileNode(String name, File pathToParent) {
        File file = new File(pathToParent, name);

        logger.debug("Loading '"+name+"'.");
        if(!file.exists())
        {
            logger.debug("Loading '"+name+"' from resources.");
            if(!copyResource(name, file.toPath(), StandardCopyOption.REPLACE_EXISTING))
                return null;
        }

        ConfigurationLoader<CommentedConfigurationNode> loader =
                HoconConfigurationLoader.builder().setPath(file.toPath()).build();
        ConfigurationNode rootNode;

        try {
            rootNode = loader.load();
        }
        catch(IOException e) {
            logger.error("'"+name+"' not loaded.", e);
            return null;
        }

        return rootNode;
    }

    public static boolean saveConfNodeToFile(ConfigurationNode node, File file)
    {
        ConfigurationLoader<CommentedConfigurationNode> loader =
                HoconConfigurationLoader.builder().setPath(file.toPath()).build();

        try {
            loader.save(node);
        } catch (IOException e) {
            logger.error("Error on saving configurationNode", e);
            return false;
        }
        return true;
    }

    public static Set<Object> getInnerRootKeys(ConfigurationNode root) {
        return root.getChildrenMap().keySet();
    }


}
