package me.doggy.justguard.utils;

import com.flowpowered.math.vector.Vector3d;
import me.doggy.justguard.JustGuard;
import me.doggy.justguard.region.GlobalRegion;
import me.doggy.justguard.region.LocalRegion;
import me.doggy.justguard.region.Region;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.util.HashMap;

import static com.google.common.base.Preconditions.checkNotNull;

public class Pending {

    private static HashMap<CommandSource, Region> regions = new HashMap<>();

    public static void createRegion(CommandSource source, Region.RegionType regionType, ConfigurationNode flags, World extent)
    {
        Region region = null;

        if(regionType.equals(Region.RegionType.Global))
            region = new GlobalRegion(flags, extent);
        else if(regionType.equals(Region.RegionType.Local))
            region = new LocalRegion(flags, new Bounds(extent));
        else
            throw new IllegalArgumentException("RegionType '"+regionType.toString()+"' not supported");

        regions.put(source, region);
    }

    public static boolean removeRegion(CommandSource source)
    {
        return regions.remove(source) != null;
    }

    public static boolean setRegionBound(CommandSource source, Bounds.BoundType boundType, Vector3d position)
    {
        Region region = regions.get(source);

        if(region == null)
            return false;
        if(!region.getRegionType().equals(Region.RegionType.Local))
            return false;

        ((LocalRegion)region).getBounds().setPosition(position, boundType);
        return true;
    }

    public static boolean uploadRegion(CommandSource source)
    {
        Region region = regions.get(source);

        if(region == null)
            return false;

        if(!JustGuard.REGIONS.add(region))
        {
            return false;
        }

        regions.remove(source);

        return true;
    }

}
