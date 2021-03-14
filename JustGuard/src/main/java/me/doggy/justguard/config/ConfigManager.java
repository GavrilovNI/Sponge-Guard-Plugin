package me.doggy.justguard.config;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.flag.Flags;
import me.doggy.justguard.flag.Groups;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.FileUtils;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

public class ConfigManager {

    private static Logger logger = JustGuard.getInstance().getLogger();

    private File configDir;
    private File regionsDir;

    private Flags defaultRegionFlags;
    private Groups groups;


    public ConfigManager(Path configDir) {
        this.configDir = configDir.toFile();
        this.regionsDir = new File(this.configDir, "regions");
    }

    public File getConfigDir() { return configDir; }
    public File getRegionsDir() { return regionsDir; }
    public Flags getDefaultRegionFlags() { return defaultRegionFlags; }
    public Groups getGroups() { return groups; }

    public File getRegionDir(Region region) {
        File worldDir = getRegionsDirByWorld(region.getWorld().getName());
        return new File(worldDir, region.getUniqueId().toString());
    }
    public File getRegionsDirByWorld(String worldName) {
        File regionsDir = getRegionsDir();
        return new File(regionsDir, worldName);
    }


    public void loadConfig() {

        this.configDir.mkdirs();
        this.regionsDir.mkdirs();

        this.defaultRegionFlags = new Flags(FileUtils.getFileNode("defaultFlags.conf", configDir));
        this.groups = new Groups(FileUtils.getFileNode("groups.conf", configDir));

        TextManager.load();

        logger.info("Config loaded.");
    }




}
