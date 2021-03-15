package me.doggy.justguard.test;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import me.doggy.justguard.JustGuard;
import me.doggy.justguard.RegionsHolder;
import me.doggy.justguard.consts.FlagKeys;
import me.doggy.justguard.flag.FlagPath;
import me.doggy.justguard.flag.FlagValue;
import me.doggy.justguard.flag.Flags;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.FlagUtils;
import me.doggy.justguard.utils.help.AABBBuilder;
import me.doggy.justguard.utils.help.MyAABB;
import ninja.leaping.configurate.ConfigurationNode;
import org.slf4j.Logger;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.ColoredData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.gen.WorldGeneratorModifiers;
import org.spongepowered.api.world.storage.WorldProperties;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TestWorld {

    private static final Logger logger = JustGuard.getInstance().getLogger();
    private static final String worldId = JustGuard.PLUGIN_ID+".test-world";

    public static Optional<World> getWorld() { return Sponge.getServer().getWorld(worldId); }

    private static FlagPath playerStrangerPrefix = FlagUtils.getPlayerPrefixFlagPath(Region.PlayerOwnership.Stranger);
    private static List<FlagPath> testFlags = Arrays.asList(
            FlagPath.of(playerStrangerPrefix, FlagKeys.BLOCK_PLACE),
            FlagPath.of(playerStrangerPrefix, FlagKeys.BLOCK_BREAK)
    );
    private static Region createTestRegion(int number, Flags testFlags) {
        World world = getWorld().get();

        BlockState blockState = (number % 2 == 0 ? BlockTypes.STONE : BlockTypes.COAL_BLOCK).getDefaultState();

        final int regionSize = 8;
        for(int x = 0; x < regionSize; x++) {
            for(int y = 0; y < regionSize; y++) {
                Location<World> location = world.getLocation(x, 100, y).add(number*regionSize,0,0);
                location.setBlock(blockState);
            }
        }

        MyAABB bounds = new MyAABB(new Vector3d(number*regionSize, 0, 0),
                                    new Vector3d((number+1)*regionSize, 256, regionSize));

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
        for(FlagPath flagPath : testFlags) {
            Flags flags = new Flags();
            flags.setFlag(FlagPath.of(Flags.DEFAULT_KEY), new FlagValue(true));
            flags.setFlag(flagPath, new FlagValue(false));
            flags.setFlag(textEnterPath, new FlagValue("Testing: '"+flagPath.getFullPath()+"'"));

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
