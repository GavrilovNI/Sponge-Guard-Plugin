package me.doggy.justguard.utils;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.RegionsHolder;
import me.doggy.justguard.config.ConfigManager;
import me.doggy.justguard.consts.FlagKeys;
import me.doggy.justguard.flag.FlagPath;
import me.doggy.justguard.region.Region;
import org.slf4j.Logger;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.spawn.SpawnType;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
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

    //
    // PATH
    //
    public static FlagPath getPath(BlockState blockState) {
        String id = blockState.getType().getId();
        return FlagPath.of(id);
    }
    public static FlagPath getPath(BlockSnapshot blockSnapshot) {
        return getPath(blockSnapshot.getState());
    }
    public static FlagPath getPath(Entity entity) {
        String id = entity.getType().getId();
        return FlagPath.of(id);
    }
    public static FlagPath getPath(EntitySnapshot entitySnapshot) {
        String id = entitySnapshot.getType().getId();
        return FlagPath.of(id);
    }
    public static FlagPath getPath(ItemStackSnapshot itemStackSnapshot) {
        String id = itemStackSnapshot.getType().getId();
        return FlagPath.of(id);
    }
    public static FlagPath getPath(SpawnType spawnType) {
        String id = spawnType.getId().toLowerCase();
        return FlagPath.of(id);
    }
    public static FlagPath getPath(DamageType damageType) {
        String id = damageType.getId().toLowerCase();
        return FlagPath.of(id);
    }

}
