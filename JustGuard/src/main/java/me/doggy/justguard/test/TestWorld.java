package me.doggy.justguard.test;

import com.flowpowered.math.vector.Vector3d;
import javafx.util.Pair;
import me.doggy.justguard.JustGuard;
import me.doggy.justguard.RegionsHolder;
import me.doggy.justguard.config.ConfigManager;
import me.doggy.justguard.consts.FlagKeys;
import me.doggy.justguard.flag.FlagPath;
import me.doggy.justguard.flag.FlagValue;
import me.doggy.justguard.flag.Flags;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.FileUtils;
import me.doggy.justguard.utils.FlagUtils;
import me.doggy.justguard.utils.help.MyAABB;
import ninja.leaping.configurate.ConfigurationNode;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.gen.WorldGeneratorModifiers;
import org.spongepowered.api.world.storage.WorldProperties;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TestWorld {

    private static final Logger logger = JustGuard.getInstance().getLogger();
    private static final ConfigManager configManager = JustGuard.getInstance().getConfigManager();
    private static final String worldId = JustGuard.PLUGIN_ID+".test-world";
    public static final int ISLANDS_HEIGHT = 100;
    public static final int ISLANDS_SIZE = 8;

    public static Optional<World> getWorld() { return Sponge.getServer().getWorld(worldId); }

    private static Region createTestRegion(int number, Flags testFlags) {
        World world = getWorld().get();

        BlockState blockState = (number % 2 == 0 ? BlockTypes.STONE : BlockTypes.COAL_BLOCK).getDefaultState();

        Vector3d startPosition = new Vector3d(1, 0, 0).mul(number * ISLANDS_SIZE);
        Vector3d endPosition = startPosition.add(ISLANDS_SIZE, 256, ISLANDS_SIZE);
        MyAABB bounds = new MyAABB(startPosition, endPosition);

        for(int x = 0; x < ISLANDS_SIZE; x++) {
            for(int y = 0; y < ISLANDS_SIZE; y++) {
                Location<World> location = new Location<World>(getWorld().get(), startPosition.add(x, ISLANDS_HEIGHT, y));
                location.setBlock(blockState);
            }
        }


        Region region = new Region(world, bounds, testFlags);

        String regionName = "test-region"+String.valueOf(number);
        while (RegionsHolder.hasRegion(regionName))
            regionName+="_";

        RegionsHolder.addRegion(regionName, region);
        return region;
    }
    private static void createTestRegions() {
        if(!getWorld().isPresent())
            return;

        FlagPath textEnterPath = FlagPath.of(FlagKeys.MESSAGES, FlagKeys.ENTER);
        int number = 0;
        for(Flags flags : configManager.getTestFlags()) {
            createTestRegion(number++, flags);
        }
    }

    public static boolean createWorld() {
        if(getWorld().isPresent())
            return false;

        WorldArchetype settings;
        Optional<WorldArchetype> settingsOpt = Sponge.getGame().getRegistry().getType(WorldArchetype.class, worldId);
        if(settingsOpt.isPresent()) {
            settings = settingsOpt.get();
        } else {
            settings = WorldArchetype.builder()
                    .enabled(true)
                    .keepsSpawnLoaded(true)
                    .loadsOnStartup(true)
                    .build(worldId, worldId);
        }

        WorldProperties properties;
        try {
            List<WorldGeneratorModifier> modifiers = Arrays.asList(WorldGeneratorModifiers.VOID);

            properties = Sponge.getServer().createWorldProperties(worldId, settings);
            properties.setGeneratorModifiers(modifiers);
        }
        catch (IOException e) {
            logger.error("Could not create test world "+ e.getMessage());
            return false;
        }

        Sponge.getServer().saveWorldProperties(properties);
        Sponge.getServer().loadWorld(Sponge.getServer().getWorldProperties(worldId).get());

        createTestRegions();

        return getWorld().isPresent();
    }


    public static boolean removeWorld() {

        Optional<WorldProperties> worldPropertiesOpt = Sponge.getServer().getWorldProperties(worldId);
        Optional<World> worldOpt = getWorld();
        if(!worldPropertiesOpt.isPresent() || !worldOpt.isPresent())
            return false;

        Location<World> defaultWorldSpawn = Sponge.getServer().getWorld(Sponge.getServer().getDefaultWorldName()).get().getSpawnLocation().add(0,10,0);

        for(Player player : Sponge.getServer().getWorld(worldId).get().getPlayers()) {
            player.setLocation(defaultWorldSpawn);
        }

        RegionsHolder.removeRegionsByWorld(Sponge.getServer().getWorld(worldId).get());
        Sponge.getServer().unloadWorld(worldOpt.get());
        Sponge.getServer().deleteWorld(worldPropertiesOpt.get());
        return true;
    }
}
