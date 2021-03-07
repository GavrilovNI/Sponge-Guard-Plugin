package me.doggy.justguard;

import com.google.inject.Inject;
import me.doggy.justguard.command.CommandsRegistrator;
import me.doggy.justguard.config.ConfigManager;
import me.doggy.justguard.events.EventRegionSelect;
import me.doggy.justguard.events.TestEventListener;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.RegionUtils;
import me.doggy.justguard.utils.help.RegionPair;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.SaveWorldEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Plugin(
        id = JustGuard.PLUGIN_ID,
        version = JustGuard.PLUGIN_VERSION,
        name = JustGuard.PLUGIN_NAME,
        description = "It's Just Guard plugin",
        authors = {
                "DoGGy"
        }
)
public class JustGuard {

    public static final String PLUGIN_ID = "justguard";
    public static final String PLUGIN_VERSION = "1.0.0";
    public static final String PLUGIN_NAME = "JustGuard";

    private static JustGuard _instance;

    public static HashMap<String, Region> REGIONS = new HashMap<>();

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;
    @Inject
    private Logger logger;

    private ConfigManager configManager;

    public static JustGuard getInstance() { return _instance; }
    public Logger getLogger() { return logger; }
    public ConfigManager getConfigManager() { return configManager; }

    @Listener
    public void preInit(GamePreInitializationEvent event)
    {
        _instance = this;

        logger.info("   _   __  ");
        logger.info("    |  | _  "+PLUGIN_NAME+" v"+PLUGIN_VERSION+" by DoGGy");
        logger.info("  \\_|  \\__) Running on Sponge - SpongeForge");
        logger.info("           ");
    }

    @Listener
    public void init(GameInitializationEvent event)
    {
        configManager = new ConfigManager(configDir);
        configManager.loadConfig();

        loadRegions();

        registerListeners();
        CommandsRegistrator.register();
    }

    private void registerListeners()
    {
        EventManager eventManager = Sponge.getEventManager();

        eventManager.registerListeners(this, new TestEventListener());
        eventManager.registerListeners(this, new EventRegionSelect());

    }

    public void loadRegions()
    {
        logger.debug("Started loading regions");
        File regionsDir = configManager.getRegionsDir();
        for (final File fileEntry : regionsDir.listFiles()) {
            if (fileEntry.isDirectory()) {
                String worldName = fileEntry.getName();
                loadRegionByWorld(worldName);
            }
        }
        logger.debug("Finished loading regions");
    }

    public void loadRegionByWorld(String worldName)
    {
        logger.debug("Started loading regions in world '"+worldName+"'");
        File worldDir = RegionUtils.getRegionsDirByWorld(worldName);
        for (final File fileEntry : worldDir.listFiles()) {
            if (fileEntry.isDirectory()) {
                RegionPair regionPair = RegionUtils.load(fileEntry);
                REGIONS.put(regionPair.name, regionPair.region);
            }
        }
        logger.debug("Finished loading regions in world '"+worldName+"'");
    }

    public void saveRegionsByWorld(String worldName)
    {
        logger.debug("Starting saving regions in world '"+worldName+"'");
        for(Map.Entry<String, Region> regionEntry : REGIONS.entrySet()) {
            String name = regionEntry.getKey();
            Region region = regionEntry.getValue();

            if(region.getWorld().getName().equals(worldName))
            {
                RegionUtils.save(new RegionPair(name, region));
            }
        }
        logger.debug("Finished saving regions in world '"+worldName+"'");
    }

    @Listener
    public void onWorldSave(SaveWorldEvent.Pre event)
    {
        saveRegionsByWorld(event.getTargetWorld().getName());
    }



}
