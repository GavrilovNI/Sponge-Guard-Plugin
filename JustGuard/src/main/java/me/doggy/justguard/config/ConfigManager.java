package me.doggy.justguard.config;

import com.flowpowered.math.vector.Vector3d;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.util.Pair;
import me.doggy.justguard.JustGuard;
import me.doggy.justguard.flag.Groups;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.FileUtils;
import me.doggy.justguard.utils.RegionUtils;
import me.doggy.justguard.utils.help.RegionPair;
import ninja.leaping.configurate.ConfigurationNode;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class ConfigManager {

    private static Logger logger = JustGuard.getInstance().getLogger();

    private File configDir;
    private File regionsDir;

    private ConfigurationNode defaultRegionFlags;
    private Groups groups;


    public ConfigManager(Path configDir) {
        this.configDir = configDir.toFile();
        this.regionsDir = new File(this.configDir, "regions");
    }

    public File getConfigDir() { return configDir; }
    public File getRegionsDir() { return regionsDir; }
    public ConfigurationNode getDefaultRegionFlags() { return defaultRegionFlags; }
    public Groups getGroups() { return groups; }

    public void loadConfig() {

        this.configDir.mkdirs();
        this.regionsDir.mkdirs();

        this.defaultRegionFlags = FileUtils.getFileNode("defaultFlags.conf", configDir);
        this.groups = new Groups(FileUtils.getFileNode("groups.conf", configDir));

        TextManager.load();

        logger.info("Config loaded.");
    }




}
