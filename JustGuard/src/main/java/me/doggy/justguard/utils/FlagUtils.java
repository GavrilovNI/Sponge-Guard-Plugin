package me.doggy.justguard.utils;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.RegionsHolder;
import me.doggy.justguard.config.ConfigManager;
import me.doggy.justguard.consts.FlagKeys;
import me.doggy.justguard.flag.FlagPath;
import me.doggy.justguard.region.Region;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

public class FlagUtils {

    private static final Logger logger = JustGuard.getInstance().getLogger();
    private static final ConfigManager configManager = JustGuard.getInstance().getConfigManager();


    public static boolean hasPlayerFlagAccess(Player player, Map<String, Region> regions, FlagPath path) {
        for (Map.Entry<String, Region> regionPair : regions.entrySet()) {
            if(!regionPair.getValue().getPlayerFlag(player, path).getBoolean(false)) {
                return false;
            }
        }
        return true;
    }
    public static boolean hasPlayerFlagAccess(Player player, Location<World> location, FlagPath path) {
        Map<String, Region> regions = RegionUtils.getHighestPriorityRegions(
                RegionsHolder.getRegions(x -> x.getValue().contains(location))
        );
        return hasPlayerFlagAccess(player, regions, path);
    }

    public static FlagPath getPlayerPrefixFlagPath(Region.PlayerOwnership ownership) {
        String playerStateKey = ownership.name().toLowerCase();
        return FlagPath.of(FlagKeys.PLAYER, playerStateKey);
    }

}
