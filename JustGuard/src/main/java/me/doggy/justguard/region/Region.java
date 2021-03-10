package me.doggy.justguard.region;

import me.doggy.justguard.utils.FileUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.apache.commons.lang3.ArrayUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;


import java.util.*;

public class Region<E extends World> {

    public enum PlayerOwnership
    {
        Owner,
        Member,
        Stranger
    }

    protected UUID uuid;

    transient private E world = null;
    private UUID worldUUID;
    private AABB bounds;

    protected Map<UUID, PlayerOwnership> playerOwnerships;
    transient protected ConfigurationNode flags;
    protected int priority;

    public Region(E world, AABB bounds, ConfigurationNode flags)
    {
        this.world = world;
        this.bounds = bounds;
        this.worldUUID = this.world.getUniqueId();
        this.uuid = UUID.randomUUID();
        this.playerOwnerships = new HashMap<>();
        this.flags = flags;
        this.priority = 0;
    }

    public UUID getUUID() { return uuid; }
    public E getWorld()
    {
        if(world == null)
            world = (E) Sponge.getServer().getWorld(worldUUID).orElse(null);
        return world;
    }
    public AABB getBounds() {
        return bounds;
    }
    public int getPriority() { return priority; }


    public void setBounds(AABB bounds) {
        this.bounds = bounds;
    }
    public ConfigurationNode getFlags() { return flags; }
    public void setFlags(ConfigurationNode flags) { this.flags = flags; }
    public void setPriority(int priority) { this.priority = priority; }

    public void setPlayerOwnership(UUID uuid, PlayerOwnership value)
    {
        if(value.equals(PlayerOwnership.Stranger))
            playerOwnerships.remove(uuid);
        else
            playerOwnerships.put(uuid, value);
    }

    public PlayerOwnership getPlayerOwnership(UUID uuid)
    {
        return playerOwnerships.getOrDefault(uuid, PlayerOwnership.Stranger);
    }

    public boolean contains(Location<World> location)
    {
        if(!world.equals(location.getExtent()))
            return false;
        return bounds.contains(location.getPosition());
    }
    public boolean intersects(Region region)
    {
        return intersects(region.world, region.bounds);
    }
    public boolean intersects(World world, AABB bounds)
    {
        if(!world.equals(world))
            return false;
        return bounds.intersects(bounds);
    }

    public ConfigurationNode getFlag(@NonNull String @NonNull... path)
    {
        return getFlag(Arrays.asList(path));
    }
    public ConfigurationNode getFlag(@NonNull Iterable<String> path) {
        return FileUtils.getFlag(flags, path);
    }
    public ConfigurationNode getPlayerFlag(Player player, @NonNull String @NonNull... path) {
        String playerStateKey;
        switch (getPlayerOwnership(player.getUniqueId()))
        {
            case Owner:
                playerStateKey = "owner";
                break;
            case Member:
                playerStateKey = "member";
                break;
            default:
                playerStateKey = "other";
                break;
        }


        String[] pathPrefix = new String[] {"player", playerStateKey};
        return getFlag(ArrayUtils.addAll(pathPrefix, path));
    }





}
