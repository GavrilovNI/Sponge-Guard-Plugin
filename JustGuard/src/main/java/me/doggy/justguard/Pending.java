package me.doggy.justguard;

import com.flowpowered.math.vector.Vector3d;
import me.doggy.justguard.region.GlobalRegion;
import me.doggy.justguard.region.LocalRegion;
import me.doggy.justguard.region.Region;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.world.World;

import java.util.HashMap;

import static com.google.common.base.Preconditions.checkNotNull;

public class Pending {

    private static HashMap<CommandSource, Region> regions = new HashMap<>();

    public static Region getRegion(CommandSource source)
    {
        return regions.get(source);
    }

    public static void createRegion(CommandSource source, Region.RegionType regionType, ConfigurationNode flags, World world)
    {
        Region region = null;

        if(regionType.equals(Region.RegionType.Global))
            region = new GlobalRegion(flags, world);
        else if(regionType.equals(Region.RegionType.Local))
            region = new LocalRegion(flags, new Bounds(world));
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

    public static boolean uploadRegion(CommandSource source, String name)
    {
        if(JustGuard.REGIONS.containsKey(name))
            return false;

        Region region = regions.get(source);

        if(region == null)
            return false;

        JustGuard.REGIONS.put(name, region);
        regions.remove(source);

        return true;
    }

}
