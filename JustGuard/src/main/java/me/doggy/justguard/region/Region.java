package me.doggy.justguard.region;

import me.doggy.justguard.consts.FlagKeys;
import me.doggy.justguard.flag.FlagPath;
import me.doggy.justguard.flag.Flags;
import me.doggy.justguard.utils.FlagUtils;
import me.doggy.justguard.utils.help.GsonableWorld;
import me.doggy.justguard.utils.help.MyAABB;
import me.doggy.justguard.flag.FlagValue;
import ninja.leaping.configurate.ConfigurationNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.Identifiable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;


import java.util.*;

public class Region<E extends World> implements Identifiable {



    public enum PlayerOwnership
    {
        Owner,
        Member,
        Stranger
    }

    private UUID uuid;
    private GsonableWorld<E> world;
    private MyAABB bounds;

    protected Map<UUID, PlayerOwnership> playerOwnerships;
    transient protected Flags flags;
    protected int priority;

    public Region(E world, MyAABB bounds, Flags flags)
    {
        this.world = new GsonableWorld(world);
        this.bounds = bounds;
        this.uuid = UUID.randomUUID();
        this.playerOwnerships = new HashMap<>();
        this.flags = flags;
        this.priority = 0;
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }
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
    public Flags getFlags() { return flags; }
    public void setFlags(Flags flags) { this.flags = flags; }
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


    private FlagPath getPlayerFlagPath(Player player) {
        return FlagUtils.getPlayerPrefixFlagPath(getPlayerOwnership(player));
    }

    @NonNull
    public FlagValue getFlag(FlagPath path) {
        return flags.getFlag(path);
    }
    @NonNull
    public FlagValue getPlayerFlag(Player player, FlagPath path) {
        return getFlag(FlagPath.of(getPlayerFlagPath(player), path));
    }

    @NonNull
    public FlagValue setFlag(FlagPath path, FlagValue value) {
        return flags.setFlag(path, value);
    }
    @NonNull
    public FlagValue setPlayerFlag(Player player, FlagPath path, FlagValue value) {
        return setFlag(FlagPath.of(getPlayerFlagPath(player), path), value);
    }

    public static class Builder {
        private Flags flags;
        private World world;
        private MyAABB.Builder aabbBuilder = MyAABB.builder();
        private int priority = 0;

        private Builder() {}

        public Builder setWorld(World world) {
            this.world = world;
            return this;
        }
        public Builder setFlags(Flags flags) {
            this.flags = flags;
            return this;
        }
        public Builder setPriority(int priority) {
            this.priority = priority;
            return this;
        }
        @Nullable
        public Flags getFlags() {
            return flags;
        }
        @Nullable
        public World getWorld() {
            return world;
        }
        public MyAABB.Builder getAABBBuilder() {
            return aabbBuilder;
        }
        public int getPriority() {
            return priority;
        }

        public Region build() {
            Region result = new Region(world, aabbBuilder.build(), flags);
            result.setPriority(priority);
            return result;
        }
    }

    public static Builder builder() {
        return new Builder();
    }


}
