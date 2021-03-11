package me.doggy.justguard.events.player;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.consts.Flags;
import me.doggy.justguard.consts.Texts;
import me.doggy.justguard.utils.FlagUtils;
import me.doggy.justguard.utils.MessageUtils;
import org.slf4j.Logger;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.TileEntityArchetype;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.manipulator.immutable.tileentity.ImmutableSignData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.tileentity.TargetTileEntityEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKey;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.function.Function;

public class PlayerEventListener {

    private static final JustGuard plugin = JustGuard.getInstance();
    private static final Logger logger = plugin.getLogger();


    private String getId(BlockSnapshot blockSnapshot) {
        return blockSnapshot.getState().getType().getId();
    }
    private String getId(Entity entity) {
        return entity.getType().getId();
    }
    private String getId(ItemStackSnapshot itemStackSnapshot) {
        return itemStackSnapshot.getType().getId();
    }
    private boolean isTileEntity(BlockSnapshot blockSnapshot)
    {
        Optional<Location<World>> locationOpt = blockSnapshot.getLocation();
        if(!locationOpt.isPresent()) {
            return blockSnapshot.createArchetype().isPresent();
        }

        return locationOpt.get().getTileEntity().isPresent();
    }

    private <T extends BlockSnapshot> boolean checkTransactions(Player player, List<Transaction<T>> transactions, Function<Transaction, T> transactionToBlockSnapshot, String ... flagPath) {
        List<String> prefixFlagPath = Arrays.asList(flagPath);

        for(Transaction<T> transaction : transactions)
        {
            BlockSnapshot blockSnapshot = transactionToBlockSnapshot.apply(transaction);
            String blockId = getId(blockSnapshot);

            ArrayList<String> currFlagPath = new ArrayList<String>(prefixFlagPath);

            boolean tileEntity = isTileEntity(blockSnapshot);

            if(tileEntity)
                currFlagPath.add(Flags.TILE_ENTITIES);

            logger.info(blockId);
            currFlagPath.add(blockId);

            Optional<Location<World>> locationOpt = transaction.getOriginal().getLocation();

            if(!locationOpt.isPresent()) {
                logger.info("location not found");
                continue;
            }

            boolean hasPlayerPermission =
                    FlagUtils.hasPlayerPermission(player, locationOpt.get(), currFlagPath);
            if(!hasPlayerPermission) {
                return false;
            }
        }
        return true;
    }

    @Listener
    public void onInteractBlockByPlayer_Primary(InteractBlockEvent.Primary event, @First Player player) {

        BlockSnapshot block = event.getTargetBlock();
        String blockId = getId(block);
        Optional<Location<World>> locationOpt = block.getLocation();

        logger.info("InteractBlockEvent.Primary: "+blockId);

        if(!locationOpt.isPresent()) {
            logger.info("location not found");
            return;
        }

        ArrayList<String> flagPath = new ArrayList<String>(Arrays.asList(Flags.BLOCK_INTERACT, Flags.PRIMARY, blockId));
        if(isTileEntity(block))
            flagPath.add(flagPath.size() - 1,Flags.TILE_ENTITIES);

        boolean hasPlayerPermission =
                FlagUtils.hasPlayerPermission(player, locationOpt.get(), flagPath);
        if(!hasPlayerPermission)
        {
            MessageUtils.SendError(player, Text.of(Texts.YOU_CANT_DO_THIS));
            logger.info("canceled");
            event.setCancelled(true);
            return;
        }

    }
    @Listener
    public void onInteractBlockByPlayer_Secondary(InteractBlockEvent.Secondary event, @First Player player) {

        BlockSnapshot block = event.getTargetBlock();
        String blockId = getId(block);
        Optional<Location<World>> locationOpt = block.getLocation();

        logger.info("InteractBlockEvent.Secondary: "+blockId);

        if(!locationOpt.isPresent()) {
            logger.info("location not found");
            return;
        }
        Location<World> location = locationOpt.get();

        ArrayList<String> flagPath = new ArrayList<String>(Arrays.asList(Flags.BLOCK_INTERACT, Flags.SECONDARY, blockId));
        if(isTileEntity(block))
            flagPath.add(flagPath.size() - 1,Flags.TILE_ENTITIES);

        boolean hasPlayerPermission =
                FlagUtils.hasPlayerPermission(player, location, flagPath);
        if(!hasPlayerPermission)
        {
            MessageUtils.SendError(player, Text.of(Texts.YOU_CANT_DO_THIS));
            logger.info("canceled");
            event.setCancelled(true);
            return;
        }

    }

    @Listener
    public void onInteractInventoryByPlayer_Open(InteractInventoryEvent.Open event, @First Player player) {
        logger.info("InteractInventoryEvent.Open");

        EventContext context = event.getContext();

        Optional<BlockSnapshot> blockHitOpt = context.get(EventContextKeys.BLOCK_HIT);
        if(!blockHitOpt.isPresent())
            return;

        BlockSnapshot blockSnapshot = blockHitOpt.get();
        Optional<Location<World>> locationOpt = blockSnapshot.getLocation();

        if(!locationOpt.isPresent())
            return;

        String blockId = getId(blockSnapshot);

        boolean hasPlayerPermission =
                FlagUtils.hasPlayerPermission(player, locationOpt.get(), Arrays.asList(Flags.INVENTORY_INTERACT, Flags.OPEN, blockId));
        if(!hasPlayerPermission)
        {
            MessageUtils.SendError(player, Text.of(Texts.YOU_CANT_DO_THIS));
            logger.info("canceled");
            event.setCancelled(true);
            return;
        }
    }

    @Listener
    public void onChangeBlockByPlayer_Break(ChangeBlockEvent.Break event, @First Player player) {
        logger.info("ChangeBlockEvent.Break");

        List<Transaction<BlockSnapshot>> transactions = event.getTransactions();


        if(!checkTransactions(player, transactions, t->(BlockSnapshot) t.getOriginal(), Flags.BLOCK_BREAK)) {
            MessageUtils.SendError(player, Text.of(Texts.YOU_CANT_DO_THIS));
            logger.info("canceled");
            event.setCancelled(true);
        }
    }
    @Listener
    public void onChangeBlockByPlayer_Place(ChangeBlockEvent.Place event, @First Player player) {
        logger.info("ChangeBlockEvent.Place");

        List<Transaction<BlockSnapshot>> transactions = event.getTransactions();

        if(!checkTransactions(player, transactions, t->(BlockSnapshot) t.getFinal(), Flags.BLOCK_PLACE)) {
            MessageUtils.SendError(player, Text.of(Texts.YOU_CANT_DO_THIS));
            logger.info("canceled");
            event.setCancelled(true);
        }
    }

    @Listener
    public void onInteractEntityByPlayer_Primary(InteractEntityEvent.Primary event, @First Player player) {
        Entity targetEntity = event.getTargetEntity();
        String entityId = getId(targetEntity);

        logger.info("InteractEntityEvent.Primary: "+entityId);

        boolean hasPlayerPermission =
                FlagUtils.hasPlayerPermission(player, targetEntity.getLocation(), Arrays.asList(Flags.ENTITY_INTERACT, Flags.PRIMARY, entityId));
        if(!hasPlayerPermission)
        {
            MessageUtils.SendError(player, Text.of(Texts.YOU_CANT_DO_THIS));
            logger.info("canceled");
            event.setCancelled(true);
            return;
        }
    }
    @Listener
    public void onInteractEntityByPlayer_Secondary(InteractEntityEvent.Secondary event, @First Player player) {

        Entity targetEntity = event.getTargetEntity();
        String entityId = getId(targetEntity);

        logger.info("InteractEntityEvent.Secondary: "+entityId);

        boolean hasPlayerPermission =
                FlagUtils.hasPlayerPermission(player, targetEntity.getLocation(), Arrays.asList(Flags.ENTITY_INTERACT, Flags.SECONDARY, entityId));
        if(!hasPlayerPermission)
        {
            MessageUtils.SendError(player, Text.of(Texts.YOU_CANT_DO_THIS));
            logger.info("canceled");
            event.setCancelled(true);
            return;
        }
    }

    @Listener
    public void onInteractItemByPlayer_Primary(InteractItemEvent.Primary event, @First Player player) {
        String itemId = getId(event.getItemStack());

        logger.info("InteractItemEvent.Primary: "+itemId);

        boolean hasPlayerPermission =
                FlagUtils.hasPlayerPermission(player, player.getLocation(), Arrays.asList(Flags.ITEM_INTERACT, Flags.PRIMARY, itemId));
        if(!hasPlayerPermission)
        {
            MessageUtils.SendError(player, Text.of(Texts.YOU_CANT_DO_THIS));
            logger.info("canceled");
            event.setCancelled(true);
            return;
        }
    }
    @Listener
    public void onInteractItemByPlayer_Secondary(InteractItemEvent.Secondary event, @First Player player) {
        String itemId = getId(event.getItemStack());

        logger.info("InteractItemEvent.Secondary: "+itemId);

        boolean hasPlayerPermission =
                FlagUtils.hasPlayerPermission(player, player.getLocation(), Arrays.asList(Flags.ITEM_INTERACT, Flags.SECONDARY, itemId));
        if(!hasPlayerPermission)
        {
            MessageUtils.SendError(player, Text.of(Texts.YOU_CANT_DO_THIS));
            logger.info("canceled");
            event.setCancelled(true);
            return;
        }
    }

    @Listener
    public void onPlayerSendCommand(SendCommandEvent event, @First Player player) {

        String command = event.getCommand();

        logger.info("SendCommandEvent");

        boolean hasPlayerPermission =
                FlagUtils.hasPlayerPermission(player, player.getLocation(), Arrays.asList(Flags.SEND_COMMAND, command));
        if(!hasPlayerPermission)
        {
            MessageUtils.SendError(player, Text.of(Texts.YOU_CANT_DO_THIS));
            logger.info("canceled");
            event.setCancelled(true);
            return;
        }
    }

    @Listener
    public void onDamagePlayer(DamageEntityEvent event, @Root DamageSource source) {

        Entity targetEntity = event.getTargetEntity();
        if(!(event.getTargetEntity() instanceof Player))
            return;

        Player player = (Player) targetEntity;
        String damageTypeStr = source.getType().getName().toLowerCase();

        logger.info("DamageEntityEvent(onDamagePlayer): "+damageTypeStr);

        boolean hasPlayerPermission =
                FlagUtils.hasPlayerPermission(player, player.getLocation(), Arrays.asList(Flags.NOT_TAKE_DAMAGE, damageTypeStr));
        if(hasPlayerPermission)
        {
            MessageUtils.SendError(player, Text.of(Texts.YOU_CANT_DO_THIS));
            logger.info("canceled");
            event.setCancelled(true);
            return;
        }
    }

    @Listener
    public void onPlayerAttack(DamageEntityEvent event, @Root DamageSource source) {

        Entity targetEntity = event.getTargetEntity();

        if(!(source instanceof EntityDamageSource))
            return;

        EntityDamageSource entityDamageSource = (EntityDamageSource) source;

        if(!(entityDamageSource.getSource() instanceof Player))
            return;

        Player player = (Player) entityDamageSource.getSource();
        String targetEntityId = getId(targetEntity);

        logger.info("DamageEntityEvent(onPlayerAttack): "+targetEntityId);

        boolean hasPlayerPermission =
                FlagUtils.hasPlayerPermission(player, player.getLocation(), Arrays.asList(Flags.ATTACK, targetEntityId));
        if(!hasPlayerPermission)
        {
            MessageUtils.SendError(player, Text.of(Texts.YOU_CANT_DO_THIS));
            logger.info("canceled");
            event.setCancelled(true);
            return;
        }
    }






    @Listener
    public void onEntityCollide(CollideEntityEvent.Impact event, @First Player player) {

        List<Entity> entities = event.getEntities();

        logger.info("CollideEntityEvent.Impact");

        for(Entity entity : entities)
        {
            String entityId = getId(entity);

            logger.info(entityId);

            boolean hasPlayerPermission =
                    FlagUtils.hasPlayerPermission(player, entity.getLocation(), Arrays.asList(Flags.ENTITY_COLLIDE, Flags.IMPACT, entityId));
            if(!hasPlayerPermission) {
                MessageUtils.SendError(player, Text.of(Texts.YOU_CANT_DO_THIS));
                logger.info("canceled");
                event.setCancelled(true);
                return;
            }
        }
    }
}
