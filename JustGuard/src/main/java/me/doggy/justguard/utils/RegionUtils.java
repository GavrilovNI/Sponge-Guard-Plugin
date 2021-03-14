package me.doggy.justguard.utils;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.ConfigManager;
import me.doggy.justguard.consts.Permissions;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.help.MyAABB;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class RegionUtils {

    private static ConfigManager configManager = JustGuard.getInstance().getConfigManager();
    private static Logger logger = JustGuard.getInstance().getLogger();

    public static Map<String, Region> getHighestPriorityRegions(Map<String, Region> regions) {
        Map<String, Region> result = new HashMap<>();

        int priority = 0;
        for (Map.Entry<String, Region> regionPair : regions.entrySet())
        {
            int currWeight = regionPair.getValue().getPriority();
            if(currWeight > priority) {
                result.clear();
                priority = currWeight;
                result.put(regionPair.getKey(), regionPair.getValue());
            }
            else if (currWeight == priority) {
                result.put(regionPair.getKey(), regionPair.getValue());
            }
        }

        return result;
    }
    public static boolean canModifyByCommand(Region region, CommandSource source) {
        if(source.hasPermission(Permissions.CAN_MODIFY_NON_OWNING_REGIONS))
            return true;

        if(source instanceof Player)
            return region.isOwner((Player) source);

        return false;
    }

}
