package me.doggy.justguard;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import me.doggy.justguard.flag.Flags;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.help.PendingRegion;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.command.CommandSource;
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

    public static PendingRegion createRegion(CommandSource source, RegionType regionType, Flags flags, World world)
    {
        PendingRegion pendingRegion = new PendingRegion();
        pendingRegion.flags = flags;
        pendingRegion.world = world;

        if(regionType.equals(RegionType.Global))
        {
            pendingRegion.aabbBuilder.setFirstBlock(new Vector3i(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE));
            pendingRegion.aabbBuilder.setSecondBlock(new Vector3i(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE));
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
        PendingRegion pendingRegion = regions.get(source);
        if(pendingRegion == null)
            return false;

        return RegionsHolder.addRegion(name, pendingRegion.build());
    }

}
