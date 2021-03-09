package me.doggy.justguard.region;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Iterables;
import me.doggy.justguard.utils.ConfigUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.apache.commons.lang3.ArrayUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;


import java.util.*;

import static java.util.Objects.requireNonNull;

public class Region<E extends World> {

    public enum PlayerState
    {
        Owner,
        Member,
        Stranger
    }
    /*public enum RegionType
    {
        Global,
        Local
    }*/


    //protected final RegionType regionType;
    protected UUID uuid;

    transient private E world = null;
    private AABB bounds;
    private UUID worldUUID;

    protected Map<UUID, PlayerState> playerStates;
    transient protected ConfigurationNode flags;
    protected int weight;
    //protected String name;

    public Region(/*RegionType regionType, */E world, AABB bounds, ConfigurationNode flags)
    {
        //this.regionType = regionType;
        this.world = world;
        this.bounds = bounds;
        this.worldUUID = this.world.getUniqueId();
        this.uuid = UUID.randomUUID();
        this.playerStates = new HashMap<>();
        this.flags = flags;
        this.weight = 0;
    }

    //public RegionType getRegionType() { return regionType; }
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
    public int getWeight() { return weight; }


    public void setBounds(AABB bounds) {
        this.bounds = bounds;
    }
    public ConfigurationNode getFlags() { return flags; }
    public void setFlags(ConfigurationNode flags) { this.flags = flags; }
    public void setWeight(int weight) { this.weight = weight; }

    public void setPlayerState(UUID uuid, PlayerState playerState)
    {
        if(playerState.equals(PlayerState.Stranger))
            playerStates.remove(uuid);
        else
            playerStates.put(uuid, playerState);
    }

    public PlayerState getPlayerState(UUID uuid)
    {
        return playerStates.getOrDefault(uuid, PlayerState.Stranger);
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



    private static final String FLAG_DEFAULT_KEY = "default";
    private static final String FLAG_INHERITS_KEY = "inherits";


    private ConfigurationNode getFlag(ConfigurationNode root, @NonNull Iterable<String> path) {
        return getFlag(root, path, null);
    }
    private ConfigurationNode getFlag(ConfigurationNode root, @NonNull Iterable<String> path, String startInh) {
        String key = Iterables.getFirst(path, null);


        ConfigurationNode result = root.getNode(key);
        Iterable<String> innerPath = Iterables.skip(path, 1);

        if(!result.isVirtual())
        {
            if(Iterables.size(path) == 1)
            {
                return result;
            }
            result = getFlag(result, innerPath, null);
        }

        if(result.isVirtual())
        {
            result = root.getNode(FLAG_DEFAULT_KEY);
            if(!result.isVirtual())
            {
                return result;
            }
            else
            {
                result = root.getNode(FLAG_INHERITS_KEY);

                if(result.isVirtual())
                    return result;

                String inheritsFromName = result.getString();

                if(startInh == null)
                {
                    startInh = (String) ConfigUtils.getNodeKey(root);
                }
                if (startInh == inheritsFromName)
                {
                    return result;
                }

                ConfigurationNode inheritsFrom = root.getParent().getNode(inheritsFromName);
                return getFlag(inheritsFrom, path, startInh);
            }
        }
        else
        {
            return result;
        }
    }

    public ConfigurationNode getFlag(@NonNull String @NonNull... path)
    {
        return getFlag(Arrays.asList(path));
    }
    public ConfigurationNode getFlag(@NonNull Iterable<String> path)
    {
        return getFlag(flags, path);
    }
    public ConfigurationNode getPlayerFlag(Player player, @NonNull String @NonNull... path) {
        String playerStateKey;
        switch (getPlayerState(player.getUniqueId()))
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
