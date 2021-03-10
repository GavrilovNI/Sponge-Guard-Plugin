package me.doggy.justguard.events.player;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.consts.Flags;
import me.doggy.justguard.consts.Texts;
import me.doggy.justguard.utils.FlagUtils;
import me.doggy.justguard.utils.MessageUtils;
import org.slf4j.Logger;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerEventListener {

    private static final JustGuard plugin = JustGuard.getInstance();
    private static final Logger logger = plugin.getLogger();

    private boolean checkTransactions(Player player, List<Transaction<BlockSnapshot>> transactions, String ... flagPath)
    {
        ArrayList<String> flagPathList = new ArrayList<>(Arrays.asList(flagPath));

        for(Transaction<BlockSnapshot> transaction : transactions)
        {
            String brokenBlockId = transaction.getOriginal().getState().getId();
            flagPathList.add(brokenBlockId);

            boolean hasPlayerPermission =
                    FlagUtils.hasPlayerPermission(player, transaction.getOriginal().getLocation().get(), flagPathList);
            if(!hasPlayerPermission) {
                return false;
            }

            flagPathList.remove(flagPathList.size()-1);
        }
        return true;
    }

    @Listener
    public void onChangeBlockEventByPlayer_Break(ChangeBlockEvent.Break event, @First Player player)
    {
        logger.info("ChangeBlockEvent.Break");

        List<Transaction<BlockSnapshot>> transactions = event.getTransactions();

        if(!checkTransactions(player, transactions, Flags.BLOCK_BREAK))
        {
            MessageUtils.SendError(player, Text.of(Texts.YOU_CANT_DO_THIS));
            event.setCancelled(true);
        }
    }
    @Listener
    public void onChangeBlockEventByPlayer_Place(ChangeBlockEvent.Place event, @First Player player)
    {
        logger.info("ChangeBlockEvent.Place");

        List<Transaction<BlockSnapshot>> transactions = event.getTransactions();

        if(!checkTransactions(player, transactions, Flags.BLOCK_PLACE))
        {
            MessageUtils.SendError(player, Text.of(Texts.YOU_CANT_DO_THIS));
            event.setCancelled(true);
        }
    }

    @Listener
    public void onInteractEntityByPlayer_Primary(InteractEntityEvent.Primary event, @First Player player)
    {
        logger.info("InteractEntityEvent.Primary");

        Entity targetEntity = event.getTargetEntity();

        String brokenBlockId = targetEntity.getType().getId();
        boolean hasPlayerPermission =
                FlagUtils.hasPlayerPermission(player, targetEntity.getLocation(), Arrays.asList(Flags.ENTITY_INTERACT, "primary", brokenBlockId));
        if(!hasPlayerPermission)
        {
            MessageUtils.SendError(player, Text.of(Texts.YOU_CANT_DO_THIS));
            event.setCancelled(true);
            return;
        }
    }

    @Listener
    public void onInteractEntityByPlayer_Secondary(InteractEntityEvent.Secondary event, @First Player player)
    {
        logger.info("InteractEntityEvent.Secondary");

        Entity targetEntity = event.getTargetEntity();

        String brokenBlockId = targetEntity.getType().getId();
        boolean hasPlayerPermission =
                FlagUtils.hasPlayerPermission(player, targetEntity.getLocation(), Arrays.asList(Flags.ENTITY_INTERACT, "secondary", brokenBlockId));
        if(!hasPlayerPermission)
        {
            MessageUtils.SendError(player, Text.of(Texts.YOU_CANT_DO_THIS));
            event.setCancelled(true);
            return;
        }
    }
}
