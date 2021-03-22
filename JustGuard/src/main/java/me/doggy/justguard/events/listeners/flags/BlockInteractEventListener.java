package me.doggy.justguard.events.listeners.flags;

import me.doggy.justguard.consts.FlagKeys;
import me.doggy.justguard.flag.FlagPath;
import me.doggy.justguard.utils.FlagUtils;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;

import java.util.Optional;

public class BlockInteractEventListener {

    //
    // BLOCKS
    //
    @Listener
    public void onPlayerInteractBlock_Primary(InteractBlockEvent.Primary event, @First Player player) {
        BlockSnapshot blockSnapshot = event.getTargetBlock();
        FlagPath flagPath = FlagPath.of(FlagKeys.BLOCK, FlagKeys.INTERACT, FlagKeys.PRIMARY, FlagUtils.getPath(blockSnapshot));
        FlagEventsHandler.handleEvent(event, player, blockSnapshot.getLocation(), flagPath);
    }
    @Listener
    public void onPlayerInteractBlock_Secondary(InteractBlockEvent.Secondary event, @First Player player) {
        BlockSnapshot blockSnapshot = event.getTargetBlock();
        FlagPath flagPath = FlagPath.of(FlagKeys.BLOCK, FlagKeys.INTERACT, FlagKeys.SECONDARY, FlagUtils.getPath(blockSnapshot));
        FlagEventsHandler.handleEvent(event, player, blockSnapshot.getLocation(), flagPath);
    }
    @Listener
    public void onPlayerCollideBlock(CollideBlockEvent event, @First Player player) {
        BlockState blockState = event.getTargetBlock();
        FlagPath flagPath = FlagPath.of(FlagKeys.BLOCK, FlagKeys.COLLIDE, FlagUtils.getPath(blockState));
        FlagEventsHandler.handleEvent(event, player, event.getTargetLocation(), flagPath);
    }
    @Listener
    public void onPlayerBreakBlock(ChangeBlockEvent.Break event, @First Player player) {
        for(Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            BlockSnapshot blockSnapshot = transaction.getOriginal();

            FlagPath flagPath = FlagPath.of(FlagKeys.BLOCK, FlagKeys.BREAK, FlagUtils.getPath(blockSnapshot));
            if(FlagEventsHandler.handleEvent(event, player, blockSnapshot.getLocation(), flagPath))
                return;
        }
    }
    @Listener
    public void onPlayerPlaceBlock(ChangeBlockEvent.Place event, @First Player player) {
        for(Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            BlockSnapshot blockSnapshot = transaction.getFinal();
            FlagPath flagPath = FlagPath.of(FlagKeys.BLOCK, FlagKeys.PLACE, FlagUtils.getPath(blockSnapshot));
            if(FlagEventsHandler.handleEvent(event, player, blockSnapshot.getLocation(), flagPath))
                return;
        }
    }
    @Listener
    public void onPlayerOpenBlockInventory(InteractInventoryEvent.Open event, @First Player player) {
        EventContext context = event.getContext();
        Optional<BlockSnapshot> blockHitOpt = context.get(EventContextKeys.BLOCK_HIT);
        if(!blockHitOpt.isPresent())
            return;

        BlockSnapshot blockSnapshot = blockHitOpt.get();
        FlagPath flagPath = FlagPath.of(FlagKeys.BLOCK, FlagKeys.OPEN_INVENTORY, FlagUtils.getPath(blockSnapshot));
        FlagEventsHandler.handleEvent(event, player, blockSnapshot.getLocation(), flagPath);
    }

}
