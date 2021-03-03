package me.doggy.justguard.region;

import me.doggy.justguard.utils.Bounds;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.util.HashMap;
import java.util.UUID;

public class LocalRegion<E extends World> extends Region {


    private Bounds<E> bounds;


    public LocalRegion(ConfigurationNode flags, Bounds<E> bounds)
    {
        super(RegionType.Local, flags);
        this.bounds = bounds;
    }

    public Bounds<E> getBounds() {
        return bounds;
    }

    public void setBounds(Bounds<E> bounds) {
        this.bounds = bounds;
    }

    @Override
    public World getWorld() { return bounds.getExtent(); }
    @Override
    public boolean isInside(Location location) {
        return bounds.isInside(location);
    }


}
