package me.doggy.justguard.events.listeners.flags;

import me.doggy.justguard.flag.FlagPath;
import me.doggy.justguard.utils.FlagUtils;
import me.doggy.justguard.utils.MessageUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class FlagEventsHandler {

    public static boolean handleEvent(Cancellable event, Player player, Location<World> location, FlagPath flagPath, boolean valueFlagAccess) {
        if(FlagUtils.hasPlayerFlagAccess(player, location, flagPath) != valueFlagAccess) {
            event.setCancelled(true);
            MessageUtils.sendErrorNoFlagAccess(player, flagPath);
            return true;
        }
        return false;
    }
    public static boolean handleEvent(Cancellable event, Player player, Optional<Location<World>> locationOpt, FlagPath flagPath, boolean valueFlagAccess) {
        if(!locationOpt.isPresent())
            return false;
        return handleEvent(event, player, locationOpt.get(), flagPath, valueFlagAccess);
    }
    public static boolean handleEvent(Cancellable event, Player player, Location<World> location, FlagPath flagPath) {
        return handleEvent(event, player, location, flagPath, true);
    }
    public static boolean handleEvent(Cancellable event, Player player, Optional<Location<World>> locationOpt, FlagPath flagPath) {
        return handleEvent(event, player, locationOpt, flagPath, true);
    }
}
