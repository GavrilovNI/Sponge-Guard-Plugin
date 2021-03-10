package me.doggy.justguard;

import com.flowpowered.math.vector.Vector3d;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.help.PendingRegion;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.World;

import java.util.HashMap;

import static com.google.common.base.Preconditions.checkNotNull;

public class Pending {

    public enum RegionType
    {
        Global,
        Local
    }

    private static HashMap<CommandSource, PendingRegion> regions = new HashMap<>();

    public static PendingRegion getRegion(CommandSource source)
    {
        return regions.get(source);
    }

    public static PendingRegion createRegion(CommandSource source, RegionType regionType, ConfigurationNode flags, World world)
    {
        PendingRegion pendingRegion = new PendingRegion();
        pendingRegion.flags = flags;
        pendingRegion.world = world;

        if(regionType.equals(RegionType.Global))
        {
            pendingRegion.aabbBuilder.setFirst(new Vector3d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY));
            pendingRegion.aabbBuilder.setSecond(new Vector3d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
            pendingRegion.priority = Integer.MIN_VALUE;
        }

        regions.put(source, pendingRegion);
        return pendingRegion;
    }

    public static boolean removeRegion(CommandSource source)
    {
        return regions.remove(source) != null;
    }

    public static boolean uploadRegion(CommandSource source, String name)
    {
        if(JustGuard.REGIONS.containsKey(name))
            return false;

        PendingRegion pendingRegion = regions.get(source);
        if(pendingRegion == null)
            return false;

        Region region = pendingRegion.build();
        regions.remove(source);

        JustGuard.REGIONS.put(name, region);

        return true;
    }

}
