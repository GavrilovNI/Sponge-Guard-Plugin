package me.doggy.justguard.utils;

import com.google.common.collect.Iterables;
import com.google.common.io.Resources;
import com.google.common.reflect.TypeToken;
import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.ConfigManager;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;

import javax.annotation.Resource;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

public class FileUtils {

    private static final Logger logger = JustGuard.getInstance().getLogger();


    @Nullable
    public static InputStream getResourceAsStream(String name) {
        ClassLoader classLoader = ConfigManager.class.getClassLoader();
        InputStream stream = classLoader.getResourceAsStream(name);
        return stream == null ? null : stream;
    }

    public static boolean copyResource(String name, File target, boolean onlyIfNotExists) {
        if(target.exists() && onlyIfNotExists)
            return false;
        InputStream stream = getResourceAsStream(name);

        target.mkdirs();

        boolean copied = false;
        if (stream != null) {
            try {
                Files.copy(stream, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                copied = true;
            }
            catch (IOException e) {
                logger.error("Error on copying resource '"+name+"'.", e);
            }
            finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    logger.error("Error on closing stream.", e);
                }
            }
        } else {
            logger.error("Can't find resource '"+name+"'.");
        }

        return copied;
    }
    public static boolean copyResource(String name, File target) {
        return copyResource(name, target, false);
    }
    @Nullable
    public static ConfigurationNode getFileNode(File path) {
        ConfigurationLoader<CommentedConfigurationNode> loader =
                HoconConfigurationLoader.builder().setPath(path.toPath()).build();
        ConfigurationNode rootNode;

        try {
            rootNode = loader.load();
        } catch(IOException e) {
            logger.error("File '"+path.getPath()+"' not loaded.", e);
            return null;
        }

        return rootNode;
    }
    @Nullable
    public static ConfigurationNode getFileNodeUsingResources(File path, String resourcePath) {
        copyResource(resourcePath, path, true);
        return path.exists() ? getFileNode(path) : null;
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

    public static String removeExtention(String name) {
        return name.replaceFirst("[.][^.]+$", "");
    }


}
