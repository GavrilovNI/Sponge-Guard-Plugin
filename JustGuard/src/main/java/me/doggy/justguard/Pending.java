package me.doggy.justguard;

import com.flowpowered.math.vector.Vector3i;
import me.doggy.justguard.flag.Flags;
import me.doggy.justguard.region.Region;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.world.World;

import java.util.HashMap;

import static com.google.common.base.Preconditions.checkNotNull;

public class Pending {

    public enum RegionType
    {
        Global,
        Local
    }

    private static HashMap<CommandSource, Region.Builder> regions = new HashMap<>();

    public static Region.Builder getRegion(CommandSource source)
    {
        return regions.get(source);
    }

    public static Region.Builder createRegion(CommandSource source, RegionType regionType, Flags flags, World world)
    {
        Region.Builder pendingRegion = Region.builder().setFlags(flags).setWorld(world);
        if(regionType.equals(RegionType.Global))
        {
            pendingRegion.setPriority(Integer.MIN_VALUE).getAABBBuilder()
                    .setFirstBlock(new Vector3i(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE))
                    .setSecondBlock(new Vector3i(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE));
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
        Region.Builder pendingRegion = regions.get(source);
        if(pendingRegion == null)
            return false;

        return RegionsHolder.addRegion(name, pendingRegion.build());
    }

}
