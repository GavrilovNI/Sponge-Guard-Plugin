package me.doggy.justguard.region;

import com.google.common.collect.Iterables;
import me.doggy.justguard.utils.ConfigUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.apache.commons.lang3.ArrayUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;


import java.util.*;

import static java.util.Objects.requireNonNull;

public abstract class Region {

    public enum PlayerState
    {
        Owner,
        Member,
        Other
    }
    public enum RegionType
    {
        Global,
        Local
    }


    protected final RegionType regionType;
    protected UUID uuid;
    protected Map<UUID, PlayerState> playerStates;
    transient protected ConfigurationNode flags;

    public Region(RegionType regionType, ConfigurationNode flags)
    {
        this.regionType = regionType;
        this.uuid = UUID.randomUUID();
        this.playerStates = new HashMap<>();
        this.flags = flags;
    }

    public RegionType getRegionType() { return regionType; }
    public UUID getUUID() { return uuid; }
    public abstract World getWorld();
    public abstract <E extends World> boolean isInside(Location<E> location);

    public ConfigurationNode getFlags() { return flags; }
    public void setFlags(ConfigurationNode flags) { this.flags = flags; }

    public void setPlayerState(UUID uuid, PlayerState playerState)
    {
        if(playerState.equals(PlayerState.Other))
            playerStates.remove(uuid);
        else
            playerStates.put(uuid, playerState);
    }

    public PlayerState getPlayerState(UUID uuid)
    {
        return playerStates.getOrDefault(uuid, PlayerState.Other);
    }




    private static final String FLAG_DEFAULT_KEY = "default";
    private static final String FLAG_INHERITS_KEY = "inherits";


    private ConfigurationNode getFlag(ConfigurationNode root, @NonNull Iterable<String> path) {
        return getFlag(root, path, null);
    }
    private ConfigurationNode getFlag(ConfigurationNode root, @NonNull Iterable<String> path, String startInh)
    {
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
    public ConfigurationNode getPlayerFlag(Player player, @NonNull String @NonNull... path)
    {
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
