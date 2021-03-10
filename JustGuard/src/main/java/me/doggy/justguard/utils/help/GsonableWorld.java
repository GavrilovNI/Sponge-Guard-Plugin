package me.doggy.justguard.utils.help;

import me.doggy.justguard.JustGuard;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;

import java.util.UUID;

public class GsonableWorld<E extends World> {
    transient private E world;
    private UUID uuid;
    private String name;

    public GsonableWorld(E world)
    {
        this.world = world;
        this.uuid = this.world.getUniqueId();
    }

    public E get()
    {
        if(world == null)
            world = (E) Sponge.getServer().getWorld(uuid).orElse(null);
        if(world == null)
            world = (E) Sponge.getServer().getWorld(name).orElse(null);
        if(world == null)
            JustGuard.getInstance().getLogger().error("World with uuid '"+ uuid.toString()+"' not found.");
        return world;
    }

    public UUID getUUID()
    {
        return uuid;
    }

}
