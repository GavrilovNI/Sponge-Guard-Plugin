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
    private static final ConfigManager configManager = JustGuard.getInstance().getConfigManager();

    public static final String KEY_DEFAULT = "default";
    public static final String KEY_INHERITS = "inherits";
    public static final String KEY_VALUES = "values";


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

        try
        {
            rootNode = loader.load();
        }
        catch(IOException e)
        {
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

    private static ConfigurationNode getFlag(ConfigurationNode root, @NonNull Iterable<String> path, String startInh) {
        String key = Iterables.getFirst(path, null);


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
        return getFlag(root, path, null);
    }
    public static ConfigurationNode getFlag(ConfigurationNode root, String ... path) {
        return getFlag(root, Arrays.asList(path), null);
    }



}
