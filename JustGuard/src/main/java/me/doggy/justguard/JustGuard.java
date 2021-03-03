package me.doggy.justguard;

import com.google.inject.Inject;
import me.doggy.justguard.command.CommandsRegistrator;
import me.doggy.justguard.config.ConfigManager;
import me.doggy.justguard.events.TestEventListener;
import me.doggy.justguard.region.Region;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Plugin;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.UUID;

@Plugin(
        id = JustGuard.PLUGIN_ID,
        version = JustGuard.PLUGIN_VERSION,
        name = "JustGuard",
        description = "It's Just Guard plugin",
        authors = {
                "DoGGy"
        }
)
public class JustGuard {

    public static final String PLUGIN_ID = "justguard";
    public static final String PLUGIN_VERSION = "1.0.0";

    private static JustGuard _instance;

    public static List<Region> REGIONS = new ArrayList<>();

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;
    @Inject
    private Logger _logger;

    private ConfigManager _configManager;

    public static JustGuard getInstance() { return _instance; }
    public Logger getLogger() { return _logger; }
    public ConfigManager getConfigManager() { return _configManager; }

    @Listener
    public void preInit(GamePreInitializationEvent event)
    {
        _instance = this;

        _logger.info("  __   __  ");
        _logger.info(" (__  | _  SafeGuard v"+PLUGIN_VERSION+" by DoGGy");
        _logger.info("  __) \\__) Running on Sponge - SpongeForge");
        _logger.info("           ");
    }

    @Listener
    public void init(GameInitializationEvent event)
    {
        _configManager = new ConfigManager(configDir);
        _configManager.loadConfig();

        registerListeners();

        CommandsRegistrator.register();
    }

    private void registerListeners()
    {
        EventManager eventManager = Sponge.getEventManager();

        eventManager.registerListeners(this, new TestEventListener());

    }


}
