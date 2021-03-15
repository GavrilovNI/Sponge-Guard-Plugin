package me.doggy.justguard.events;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.region.Region;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;

public class PlayerEnterRegionEvent implements Cancellable, TargetPlayerEvent {
    private boolean cancelled = false;

    private final Player player;
    private final Region region;
    private final Cause cause;

    public PlayerEnterRegionEvent(Player player, Region region) {
        this.player = player;
        this.region = region;
        EventContext eventEnterContext = EventContext.builder().add(EventContextKeys.PLAYER, player).build();
        this.cause = Cause.of(eventEnterContext, JustGuard.getInstance().getPluginContainer());
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public Cause getCause() {
        return cause;
    }

    @Override
    public Player getTargetEntity() {
        return player;
    }

    public Region getRegion() {
        return region;
    }
}
