package me.doggy.justguard;

import com.google.inject.Inject;
import me.doggy.justguard.command.CommandsRegistrator;
import me.doggy.justguard.config.ConfigManager;
import me.doggy.justguard.events.EventRegionSelect;
import me.doggy.justguard.events.player.PlayerEventListener;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.RegionUtils;
import me.doggy.justguard.utils.help.RegionPair;
import net.luckperms.api.LuckPerms;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.SaveWorldEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.world.World;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

@Plugin(
        id = JustGuard.PLUGIN_ID,
        version = JustGuard.PLUGIN_VERSION,
        name = JustGuard.PLUGIN_NAME,
        description = "It's Just Guard plugin",
        authors = {
                "DoGGy"
        },
        dependencies = {
                @Dependency(id = "luckperms", optional = false)
        }
)
public class JustGuard {

    public static final String PLUGIN_ID = "justguard";
    public static final String PLUGIN_VERSION = "1.0.0";
    public static final String PLUGIN_NAME = "JustGuard";

    private static JustGuard _instance;

    public static HashMap<String, Region> REGIONS = new HashMap<>();
    private static List<RegionPair> REGIONS_TO_REMOVE = new ArrayList<>();

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;
    @Inject
    private Logger logger;

    private ConfigManager configManager;
    private LuckPerms luckPerms;

    public static JustGuard getInstance() { return _instance; }
    public Logger getLogger() { return logger; }
    public ConfigManager getConfigManager() { return configManager; }
    public LuckPerms getLuckPerms() { return luckPerms; }

    @Listener
    public void preInit(GameConstructionEvent event)
    {
        _instance = this;
    }

    @Listener
    public void preInit(GamePreInitializationEvent event)
    {
        configManager = new ConfigManager(configDir);
        configManager.loadConfig();
    }

    @Listener
    public void init(GameInitializationEvent event)
    {
        loadRegions();
        registerListeners();
    }

    @Listener
    public void serverStarting(GameStartingServerEvent event)
    {
        CommandsRegistrator.register();
    }

    @Listener
    public void loadComplete(GameLoadCompleteEvent event)
    {
        boolean loaded = false;
        Optional<ProviderRegistration<LuckPerms>> provider = Sponge.getServiceManager().getRegistration(LuckPerms.class);
        if (provider.isPresent()) {
            luckPerms = provider.get().getProvider();
            loaded = true;
        }
        else {
            logger.error("LuckPerms api not found!");
        }

        if(loaded)
        {
            logger.info("   _   __  ");
            logger.info("    |  | _  "+PLUGIN_NAME+" v"+PLUGIN_VERSION+" by DoGGy");
            logger.info("  \\_|  \\__) Running on Sponge - SpongeForge");
            logger.info("           ");
        }

    }


    private void registerListeners()
    {
        EventManager eventManager = Sponge.getEventManager();

        eventManager.registerListeners(this, new EventRegionSelect());

        //player listeners
        eventManager.registerListeners(this, new PlayerEventListener());
    }

    public void loadRegions() {
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
    public void loadRegionByWorld(String worldName) {
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
    public void saveRegionsByWorld(String worldName) {
        logger.debug("Starting saving regions in world '"+worldName+"'");
        for(Map.Entry<String, Region> regionEntry : REGIONS.entrySet()) {
            String name = regionEntry.getKey();
            Region region = regionEntry.getValue();

            World regionWorld = region.getWorld();
            if(regionWorld != null && regionWorld.getName().equals(worldName))
            {
                RegionUtils.save(new RegionPair(name, region));
            }
        }
        for(RegionPair regionPair : REGIONS_TO_REMOVE) {
            RegionUtils.removeRegionFromFiles(regionPair.region);
        }
        REGIONS_TO_REMOVE.clear();
        logger.debug("Finished saving regions in world '"+worldName+"'");
    }
    public void saveRegions() {
        logger.debug("Starting saving regions.");
        for(Map.Entry<String, Region> regionEntry : REGIONS.entrySet()) {
            String name = regionEntry.getKey();
            Region region = regionEntry.getValue();
            RegionUtils.save(new RegionPair(name, region));
        }
        for(RegionPair regionPair : REGIONS_TO_REMOVE) {
            RegionUtils.removeRegionFromFiles(regionPair.region);
        }
        REGIONS_TO_REMOVE.clear();
        logger.debug("Finished saving regions.");
    }


    public boolean removeRegion(String name)
    {
        Region region = REGIONS.remove(name);
        if(region == null)
            return false;
        REGIONS_TO_REMOVE.add(new RegionPair(name, region));
        return false;
    }


    @Listener
    public void onWorldSave(SaveWorldEvent.Pre event)
    {
        saveRegionsByWorld(event.getTargetWorld().getName());
    }



}
