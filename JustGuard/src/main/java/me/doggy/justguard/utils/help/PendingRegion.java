package me.doggy.justguard.utils.help;

import com.flowpowered.math.vector.Vector3d;
//import me.doggy.justguard.region.GlobalRegion;
//import me.doggy.justguard.region.LocalRegion;
import me.doggy.justguard.flag.Flags;
import me.doggy.justguard.region.Region;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.World;

public class PendingRegion{
    //public Region.RegionType regionType;
    public Flags flags;
    public World world;
    public AABBBuilder aabbBuilder = new AABBBuilder();
    public int priority = 0;

    public Region build()
    {
        Region result = new Region(world, aabbBuilder.build(), flags);
        result.setPriority(priority);

        return result;
    }
}