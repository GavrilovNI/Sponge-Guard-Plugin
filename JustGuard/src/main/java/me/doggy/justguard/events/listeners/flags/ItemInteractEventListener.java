package me.doggy.justguard.events.listeners.flags;

import me.doggy.justguard.consts.FlagKeys;
import me.doggy.justguard.flag.FlagPath;
import me.doggy.justguard.utils.FlagUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

public class ItemInteractEventListener {

    //
    // ITEMS
    //
    @Listener
    public void onPlayerInteractItem_Primary(InteractItemEvent.Primary event, @First Player player) {
        ItemStackSnapshot itemStackSnapshot = event.getItemStack();
        FlagPath flagPath = FlagPath.of(FlagKeys.ITEM, FlagKeys.INTERACT, FlagKeys.PRIMARY, FlagUtils.getPath(itemStackSnapshot));
        FlagEventsHandler.handleEvent(event, player, player.getLocation(), flagPath);
    }
    @Listener
    public void onPlayerInteractItem_Secondary(InteractItemEvent.Secondary event, @First Player player) {
        ItemStackSnapshot itemStackSnapshot = event.getItemStack();
        FlagPath flagPath = FlagPath.of(FlagKeys.ITEM, FlagKeys.INTERACT, FlagKeys.SECONDARY, FlagUtils.getPath(itemStackSnapshot));
        FlagEventsHandler.handleEvent(event, player, player.getLocation(), flagPath);
    }
    @Listener
    public void onPlayerDropItem(DropItemEvent.Pre event, @First Player player) {
        for (ItemStackSnapshot itemStackSnapshot : event.getDroppedItems()) {
            FlagPath flagPath = FlagPath.of(FlagKeys.ITEM, FlagKeys.DROP, FlagUtils.getPath(itemStackSnapshot));
            if(FlagEventsHandler.handleEvent(event, player, player.getLocation(), flagPath))
                return;
        }
    }
    @Listener
    public void onPlayerPickupItem(ChangeInventoryEvent.Pickup.Pre event, @First Player player) {
        for (ItemStackSnapshot itemStackSnapshot : event.getFinal()) {
            FlagPath flagPath = FlagPath.of(FlagKeys.ITEM, FlagKeys.PICKUP, FlagUtils.getPath(itemStackSnapshot));
            if(FlagEventsHandler.handleEvent(event, player, player.getLocation(), flagPath))
                return;
        }
    }
    @Listener
    public void onPlayerUseItem(UseItemStackEvent.Start event, @First Player player) {
        ItemStackSnapshot itemStackSnapshot = event.getItemStackInUse();
        FlagPath flagPath = FlagPath.of(FlagKeys.ITEM, FlagKeys.USE, FlagUtils.getPath(itemStackSnapshot));
        FlagEventsHandler.handleEvent(event, player, player.getLocation(), flagPath);
    }

}
