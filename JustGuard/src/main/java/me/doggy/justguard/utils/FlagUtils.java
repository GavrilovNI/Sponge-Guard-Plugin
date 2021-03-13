package me.doggy.justguard.utils;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.ConfigManager;
import me.doggy.justguard.flag.FlagPath;
import me.doggy.justguard.flag.Groups;
import me.doggy.justguard.utils.help.RegionPair;
import me.doggy.justguard.flag.FlagValue;
import ninja.leaping.configurate.ConfigurationNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

public class FlagUtils {

    private static final Logger logger = JustGuard.getInstance().getLogger();
    private static final ConfigManager configManager = JustGuard.getInstance().getConfigManager();


    public static boolean hasPlayerFlagAccess(Player player, List<RegionPair> regions, FlagPath path)
    {
        for (RegionPair regionPair : regions) {
            if(!regionPair.region.getPlayerFlag(player, path).getBoolean(false)) {
                return false;
            }
        }
        return true;
    }
    public static boolean hasPlayerFlagAccess(Player player, Location<World> location, FlagPath path)
    {
        List<RegionPair> regions = RegionUtils.getHighestPriorityRegions(RegionUtils.getRegionsInLocation(location));
        return hasPlayerFlagAccess(player, regions, path);
    }

}
