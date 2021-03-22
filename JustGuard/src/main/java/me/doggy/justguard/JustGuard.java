package me.doggy.justguard;

import com.google.inject.Inject;
import me.doggy.justguard.command.CommandsRegistrator;
import me.doggy.justguard.config.ConfigManager;
import me.doggy.justguard.events.listeners.RegionSelectEventListener;
import me.doggy.justguard.events.listeners.flags.*;
import net.luckperms.api.LuckPerms;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.SaveWorldEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.ProviderRegistration;

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

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;
    @Inject
    private Logger logger;
    @Inject
    private PluginContainer pluginContainer;

    private ConfigManager configManager;
    private LuckPerms luckPerms;

    public static JustGuard getInstance() { return _instance; }
    public PluginContainer getPluginContainer() { return pluginContainer; }
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
    public void init(GameInitializationEvent event) {
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

        eventManager.registerListeners(this, new RegionSelectEventListener());

        //player listeners
        eventManager.registerListeners(this, new BlockInteractEventListener());
        eventManager.registerListeners(this, new EntityInteractEventListener());
        eventManager.registerListeners(this, new ItemInteractEventListener());
        eventManager.registerListeners(this, new MoveEventListener());
        eventManager.registerListeners(this, new PlayerSpecificEventListener());
    }




    @Listener
    public void onWorldSave(SaveWorldEvent.Pre event) {
        RegionsHolder.saveRegionsByWorld(event.getTargetWorld());
    }
    @Listener
    public void onWorldLoad(LoadWorldEvent event) {
        RegionsHolder.loadRegionsByWorld(event.getTargetWorld());
    }



}
