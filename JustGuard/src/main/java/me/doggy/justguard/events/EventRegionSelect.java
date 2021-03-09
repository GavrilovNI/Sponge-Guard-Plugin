package me.doggy.justguard.events;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import me.doggy.justguard.JustGuard;
import me.doggy.justguard.Pending;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.config.Texts;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.InventoryUtils;
import me.doggy.justguard.utils.MessageUtils;
import me.doggy.justguard.utils.help.AABBBuilder;
import me.doggy.justguard.utils.help.PendingRegion;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class EventRegionSelect {

    @Listener
    public void onLeftClick(InteractBlockEvent.Primary event, @First Player player){
        if(onClick(player, event.getHandType(), AABBBuilder.BoundType.First, event.getTargetBlock().getPosition()))
            event.setCancelled(true);
    }
    @Listener
    public void onLeftClick(InteractBlockEvent.Secondary event, @First Player player){
        if(onClick(player, event.getHandType(), AABBBuilder.BoundType.Second, event.getTargetBlock().getPosition()))
            event.setCancelled(true);
    }

    private boolean onClick(Player player, HandType handType, AABBBuilder.BoundType boundType, Vector3i position)
    {
        if(shouldEventCause(player, handType))
        {
            PendingRegion createdRegion = Pending.getRegion(player);
            if(createdRegion == null) {
                createdRegion = Pending.createRegion(player, Pending.RegionType.Local, JustGuard.getInstance().getConfigManager().getDefaultRegionFlags(), player.getWorld());
            }
            createdRegion.aabbBuilder.set(position.toDouble(), boundType);

            MessageUtils.Send(player, Text.of(TextManager.getText(
                    Texts.CMD_ANSWER_BOUND_SETTED,
                    boundType.name().toLowerCase(),
                    position.toString()
            )));
            return true;
        }
        return false;
    }

    private boolean shouldEventCause(Player player, HandType handType)
    {
        Optional<ItemStack> itemInHandOpt = player.getItemInHand(handType);
        return itemInHandOpt.isPresent() && InventoryUtils.isSelector(itemInHandOpt.get());
    }

}
