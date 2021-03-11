package me.doggy.justguard.region;

import me.doggy.justguard.utils.FlagUtils;
import me.doggy.justguard.utils.help.GsonableWorld;
import me.doggy.justguard.utils.help.MyAABB;
import me.doggy.justguard.utils.help.Flag;
import ninja.leaping.configurate.ConfigurationNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.entity.living.player.Player;
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

    private GsonableWorld<E> world;
    private MyAABB bounds;

    protected Map<UUID, PlayerOwnership> playerOwnerships;
    transient protected ConfigurationNode flags;
    protected int priority;

    public Region(E world, MyAABB bounds, ConfigurationNode flags)
    {
        this.world = new GsonableWorld(world);
        this.bounds = bounds;
        this.uuid = UUID.randomUUID();
        this.playerOwnerships = new HashMap<>();
        this.flags = flags;
        this.priority = 0;
    }

    public UUID getUUID() { return uuid; }
    public E getWorld()
    {
        return world.get();
    }
    public MyAABB getBounds() {
        return bounds;
    }
    public int getPriority() { return priority; }


    public void setBounds(MyAABB bounds) {
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

    public PlayerOwnership getPlayerOwnership(UUID uuid) {
        return playerOwnerships.getOrDefault(uuid, PlayerOwnership.Stranger);
    }
    public PlayerOwnership getPlayerOwnership(Player player) {
        return getPlayerOwnership(player.getUniqueId());
    }
    public boolean isOwner(Player player) { return getPlayerOwnership(player).equals(PlayerOwnership.Owner); }

    public boolean contains(Location<World> location)
    {
        if(!this.getWorld().equals(location.getExtent()))
            return false;
        return bounds.contains(location.getPosition());
    }
    public boolean intersects(Region region)
    {
        return intersects(region.world.get(), region.bounds);
    }
    public boolean intersects(World world, MyAABB bounds)
    {
        if(!this.world.equals(world))
            return false;
        return this.bounds.intersects(bounds);
    }


    @NonNull
    public Flag getFlag(@NonNull Collection<String> path) {
        return FlagUtils.getFlag(flags, path);
    }

    @NonNull
    public Flag getPlayerFlag(Player player, @NonNull Collection<String> path) {
        String playerStateKey = getPlayerOwnership(player.getUniqueId()).name().toLowerCase();

        ArrayList<String> pathList = new ArrayList(Arrays.asList("player", playerStateKey));
        pathList.addAll(path);
        return getFlag(pathList);
    }





}
