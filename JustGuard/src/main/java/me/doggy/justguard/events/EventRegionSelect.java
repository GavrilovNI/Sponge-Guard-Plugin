package me.doggy.justguard.events;

import me.doggy.justguard.utils.InventoryUtils;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

public class EventRegionSelect {

    /*@Listener
    public void onLeftClick(InteractBlockEvent.Primary event, @First Player player){

        if(shouldEventCause(player, event.getHandType()))
        {
            this.setPoint(true, event);
        }
    }

    @Listener
    public void onRightClick(InteractBlockEvent.Secondary event) {
        this.setPoint(RegionPoint.SECONDARY, HandTypes.OFF_HAND, event);
    }

    private boolean shouldEventCause(Player player, HandType handType)
    {
        Optional<ItemStack> itemInHandOpt = player.getItemInHand(handType);
        return itemInHandOpt.isPresent() && InventoryUtils.isSelector(itemInHandOpt.get());
    }*/

}
