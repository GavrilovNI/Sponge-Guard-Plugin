package me.doggy.justguard.region;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public class GlobalRegion <E extends World> extends Region {

    transient private E extent = null;
    private UUID extentUUID;

    public GlobalRegion(ConfigurationNode flags, E extent)
    {
        super(RegionType.Global, flags);
        this.setExtent(extent);
    }

    public void setExtent(E extent)
    {
        this.extent = checkNotNull(extent, "extent");
        extentUUID = extent.getUniqueId();
    }

    @Override
    public World getWorld() {

        if(extent==null)
            extent = (E) Sponge.getServer().getWorld(extentUUID).orElse(null);

        return extent;
    }

    @Override
    public <E extends World> boolean isInside(Location<E> location) {
        return getWorld().equals(location.getExtent());
    }
}
