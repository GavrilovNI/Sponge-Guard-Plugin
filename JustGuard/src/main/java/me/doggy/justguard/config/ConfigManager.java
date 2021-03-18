package me.doggy.justguard.config;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.flag.Flags;
import me.doggy.justguard.flag.Groups;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.FileUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.util.Objects.requireNonNull;

public class ConfigManager {

    private static Logger logger = JustGuard.getInstance().getLogger();
    public static final String DEFAULT_REGION_FLAGS_NAME = "default";

    private File configDir;
    private File regionsDir;

    private Groups groups;
    private List<Flags> testFlags;


    public ConfigManager(Path configDir) {
        this.configDir = configDir.toFile();
        this.regionsDir = new File(this.configDir, "regions");
    }

    public File getConfigDir() { return configDir; }
    public File getRegionsDir() { return regionsDir; }
    public Groups getGroups() { return groups; }

    public File getRegionDir(Region region) {
        File worldDir = getRegionsDirByWorld(region.getWorld().getName());
        return new File(worldDir, region.getUniqueId().toString());
    }
    public File getRegionsDirByWorld(String worldName) {
        File regionsDir = getRegionsDir();
        return new File(regionsDir, worldName);
    }
    public File getFlagsDir() { return new File(configDir, "flags"); }

    private HashMap<String, Flags> flags;

    @Nullable
    public Flags getRegionFlags(String name) {
        return flags.get(name);
    }
    @Nullable
    public Flags getDefaultRegionFlags() { return getRegionFlags(DEFAULT_REGION_FLAGS_NAME); }
    public List<Flags> getTestFlags() { return testFlags; }


    private void loadFlags() {
        flags = new HashMap<>();

        File flagsDir = getFlagsDir();
        if(!flagsDir.exists() || !flagsDir.isDirectory()) {
            logger.warn("Directory '" + flagsDir.getPath() + "' not found. Creating...");
            flagsDir.delete();
            flagsDir.mkdirs();
        }

        FileUtils.copyResource("flags/default.conf", new File(flagsDir, "default.conf"), true);

        for(File flagFile : flagsDir.listFiles()) {
            String flagsName = FileUtils.removeExtention(flagFile.getName());
            ConfigurationNode flagsNode = FileUtils.getFileNode(flagFile);
            flags.put(flagsName, new Flags(flagsNode));
        }
    }

    private void loadTestFlags() {
        ConfigurationNode testFlagsNode = FileUtils.getFileNodeUsingResources(Paths.get(getConfigDir().getPath(), "test", "flags.conf").toFile(), "test/flags.conf");
        testFlags = new ArrayList<>();
        for(Object flagNodeName : testFlagsNode.getChildrenMap().keySet()) {
            ConfigurationNode flagNode = testFlagsNode.getNode(flagNodeName);
            testFlags.add(new Flags(flagNode));
        }
    }

    public void loadConfig() {

        this.configDir.mkdirs();
        this.regionsDir.mkdirs();


        this.loadFlags();
        final String groupFileName = "groups.conf";
        this.groups = new Groups(FileUtils.getFileNodeUsingResources(new File(configDir, groupFileName), groupFileName));

        this.loadTestFlags();


        TextManager.load();

        logger.info("Config loaded.");
    }




}
