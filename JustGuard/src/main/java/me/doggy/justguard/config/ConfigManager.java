package me.doggy.justguard.config;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.utils.FileUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class ConfigManager {

    private static Logger logger = JustGuard.getInstance().getLogger();

    private File configDir;
    private File regionsDir;

    public ConfigManager(Path configDir)
    {
        this.configDir = configDir.toFile();
        this.configDir.mkdirs();

        this.regionsDir = new File(this.configDir, "regions");
        this.regionsDir.mkdirs();
    }

    private Map<String, Boolean> flags;
    //private ConfigurationNode serverProperties;

    public File getConfigDir() { return configDir; }
    public File getRegionsDir() { return regionsDir; }
    public boolean getFlag(String flag)
    {
        return flags.getOrDefault(flag.toLowerCase(), true);
    }
    //public ConfigurationNode getServerProperty(String name) { return serverProperties.getNode("name"); }

    public void loadConfig() {

        loadFlags();
        TextManager.load();
        //loadServerProperties();

        logger.info("Config loaded.");
    }

    /*public void loadServerProperties()
    {
        String name = "server.properties";
        logger.debug("Loading '"+name+"'.");
        ConfigurationNode serverProperties = getFileNode(name, FileUtils.getServerDir());

    }*/

    public void loadFlags()
    {
        ConfigurationNode rootNode = FileUtils.getFileNode("flags.conf", configDir);

        flags = new HashMap<>();

        for (Map.Entry<Object, ? extends ConfigurationNode> entry : rootNode.getChildrenMap().entrySet())
        {
            String keyStr = entry.getKey().toString();
            Boolean value = entry.getValue().getBoolean(true);
            flags.put(keyStr.toLowerCase(), value);
        }
    }

    public ConfigurationNode getDefaultRegionFlags()
    {
        return FileUtils.getFileNode("defaultFlags.conf", configDir);
    }
}
