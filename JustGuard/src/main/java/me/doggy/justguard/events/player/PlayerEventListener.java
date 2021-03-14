package me.doggy.justguard.events.player;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.consts.FlagKeys;
import me.doggy.justguard.consts.Texts;
import me.doggy.justguard.flag.FlagPath;
import me.doggy.justguard.utils.FlagUtils;
import me.doggy.justguard.utils.InventoryUtils;
import me.doggy.justguard.utils.MessageUtils;
import org.slf4j.Logger;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.spawn.SpawnType;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.*;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.*;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PlayerEventListener {

    private static final JustGuard plugin = JustGuard.getInstance();
    private static final Logger logger = plugin.getLogger();

    private boolean isTileEntity(BlockSnapshot blockSnapshot) {
        Optional<Location<World>> locationOpt = blockSnapshot.getLocation();
        if(!locationOpt.isPresent()) {
            return blockSnapshot.createArchetype().isPresent();
        }
        return locationOpt.get().getTileEntity().isPresent();
    }
    private FlagPath getId(BlockSnapshot blockSnapshot) {
        String lastId = blockSnapshot.getState().getType().getId();
        FlagPath result = new FlagPath(lastId);

        if(isTileEntity(blockSnapshot))
            result.addInFront(FlagKeys.TILE_ENTITIES);

        return result;
    }
    private FlagPath getId(Entity entity) {
        String lastId = entity.getType().getId();
        FlagPath result = new FlagPath(lastId);

        return result;
    }
    private FlagPath getId(EntitySnapshot entitySnapshot) {
        String lastId = entitySnapshot.getType().getId();
        FlagPath result = new FlagPath(lastId);

        return result;
    }
    private FlagPath getId(ItemStackSnapshot itemStackSnapshot) {
        String lastId = itemStackSnapshot.getType().getId();
        FlagPath result = new FlagPath(lastId);
        return result;
    }
    private FlagPath getId(BlockState blockState) {
        String lastId = blockState.getType().getId().toLowerCase();
        FlagPath result = new FlagPath(lastId);
        return result;
    }
    private FlagPath getId(SpawnType spawnType) {
        String lastId = spawnType.getId().toLowerCase();
        FlagPath result = new FlagPath(lastId);
        return result;
    }
    private FlagPath getId(DamageType damageType) {
        String lastId = damageType.getId().toLowerCase();
        FlagPath result = new FlagPath(lastId);
        return result;
    }


    private boolean checkAndCancelIfNeeded(Cancellable event, Player player, Location<World> location, FlagPath flagPath, boolean inverted) {
        logger.info(event.getClass().getSimpleName()+": "+flagPath.getFullPath());
        if(FlagUtils.hasPlayerFlagAccess(player, location, flagPath) == inverted) {
            MessageUtils.SendError(player, Text.of(TextManager.getText(
                    Texts.DONT_HAVE_FLAG_ACCESS,
                    flagPath.getFullPath()
            )));
            event.setCancelled(true);
            logger.info(event.getClass().getSimpleName()+": "+"cancelled");
            return false;
        }
        return true;
    }
    private boolean checkAndCancelIfNeeded(Cancellable event, Player player, Location<World> location, FlagPath flagPath) {
        return checkAndCancelIfNeeded(event, player, location, flagPath, false);
    }
    private boolean checkAndCancelIfNeeded(Cancellable event, Player player, Optional<Location<World>> locationOpt, FlagPath flagPath, boolean inverted) {
        if(!locationOpt.isPresent()) {
            logger.info(event.getClass().getSimpleName()+": Location not found.");
            return true;
        }
        return checkAndCancelIfNeeded(event, player, locationOpt.get(), flagPath, inverted);
    }
    private boolean checkAndCancelIfNeeded(Cancellable event, Player player, Optional<Location<World>> locationOpt, FlagPath flagPath) {
        return checkAndCancelIfNeeded(event, player, locationOpt, flagPath, false);
    }
    private boolean checkAndCancelIfNeeded(ChangeBlockEvent event, Player player, Function<Transaction, BlockSnapshot> transactionToBlockSnapshot, FlagPath prefixPath, boolean inverted) {
        for(Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            BlockSnapshot blockSnapshot = transactionToBlockSnapshot.apply(transaction);
            if(!checkAndCancelIfNeeded(event, player, blockSnapshot.getLocation(), new FlagPath(prefixPath).add(getId(blockSnapshot)), inverted))
                return false;
        }
        return true;
    }
    private boolean checkAndCancelIfNeeded(ChangeBlockEvent event, Player player, Function<Transaction, BlockSnapshot> transactionToBlockSnapshot, FlagPath prefixPath) {
        return checkAndCancelIfNeeded(event, player, transactionToBlockSnapshot, prefixPath, false);
    }

    @Listener
    public void onPlayerInteractBlock_Primary(InteractBlockEvent.Primary event, @First Player player) {
        BlockSnapshot blockSnapshot = event.getTargetBlock();
        FlagPath flagPath = new FlagPath(FlagKeys.BLOCK_INTERACT, FlagKeys.PRIMARY).add(getId(blockSnapshot));
        checkAndCancelIfNeeded(event, player, blockSnapshot.getLocation(), flagPath);
    }
    @Listener
    public void onPlayerInteractBlock_Secondary(InteractBlockEvent.Secondary event, @First Player player) {
        BlockSnapshot blockSnapshot = event.getTargetBlock();
        FlagPath flagPath = new FlagPath(FlagKeys.BLOCK_INTERACT, FlagKeys.SECONDARY).add(getId(blockSnapshot));
        checkAndCancelIfNeeded(event, player, blockSnapshot.getLocation(), flagPath);
    }
    @Listener
    public void onPlayerCollideBlock(CollideBlockEvent event, @First Player player) {
        BlockState blockState = event.getTargetBlock();
        FlagPath flagPath = new FlagPath(FlagKeys.BLOCK_COLLIDE).add(getId(blockState));
        checkAndCancelIfNeeded(event, player, event.getTargetLocation(), flagPath);
    }

    @Listener
    public void onPlayerOpenInventory(InteractInventoryEvent.Open event, @First Player player) {
        EventContext context = event.getContext();
        Optional<BlockSnapshot> blockHitOpt = context.get(EventContextKeys.BLOCK_HIT);
        if(!blockHitOpt.isPresent())
            return;

        BlockSnapshot blockSnapshot = blockHitOpt.get();
        FlagPath flagPath = new FlagPath(FlagKeys.INVENTORY_INTERACT, FlagKeys.OPEN).add(getId(blockSnapshot));
        checkAndCancelIfNeeded(event, player, blockSnapshot.getLocation(), flagPath);
    }

    @Listener
    public void onPlayerBreakBlock(ChangeBlockEvent.Break event, @First Player player) {
        checkAndCancelIfNeeded(event, player, t->(BlockSnapshot) t.getOriginal(), new FlagPath(FlagKeys.BLOCK_BREAK));
    }
    @Listener
    public void onPlayerPlaceBlock(ChangeBlockEvent.Place event, @First Player player) {
        checkAndCancelIfNeeded(event, player, t->(BlockSnapshot) t.getFinal(), new FlagPath(FlagKeys.BLOCK_PLACE));
    }

    @Listener
    public void onPlayerInteractEntity_Primary(InteractEntityEvent.Primary event, @First Player player) {
        Entity targetEntity = event.getTargetEntity();
        FlagPath flagPath = new FlagPath(FlagKeys.ENTITY_INTERACT, FlagKeys.PRIMARY).add(getId(targetEntity));
        checkAndCancelIfNeeded(event, player, targetEntity.getLocation(), flagPath);
    }
    @Listener
    public void onPlayerInteractEntity_Secondary(InteractEntityEvent.Secondary event, @First Player player) {
        Entity targetEntity = event.getTargetEntity();
        FlagPath flagPath = new FlagPath(FlagKeys.ENTITY_INTERACT, FlagKeys.SECONDARY).add(getId(targetEntity));
        checkAndCancelIfNeeded(event, player, targetEntity.getLocation(), flagPath);
    }
    @Listener
    public void onPlayerSpawnEntity(SpawnEntityEvent event, @First Player player) {
        Optional<SpawnType> spawnTypeOpt = event.getContext().get(EventContextKeys.SPAWN_TYPE);
        if(!spawnTypeOpt.isPresent())
            return;
        if(!event.getContext().get(EventContextKeys.PLAYER_PLACE).isPresent()) // if so - thats not a placement, this can be dropped item
            return;


        for (Entity targetEntity : event.getEntities()) {
            FlagPath flagPath = new FlagPath(FlagKeys.ENTITY_SPAWN).add(getId(spawnTypeOpt.get())).add(getId(targetEntity));
            if(!checkAndCancelIfNeeded(event, player, targetEntity.getLocation(), flagPath)) {

                if(!player.gameMode().get().equals(GameModes.CREATIVE)) {
                    Optional<ItemStackSnapshot> usedItemOpt = event.getContext().get(EventContextKeys.USED_ITEM);
                    if (usedItemOpt.isPresent()) {
                        ItemStack itemStackToReturn = usedItemOpt.get().createStack();
                        itemStackToReturn.setQuantity(1);

                        //we dont return item immediately because of bug(if u return item with max stack 1, and u return it to position event has removed this from, it wont be returned)
                        InventoryUtils.addItemStackToInventoryDelayed(player, itemStackToReturn);
                    }
                }
                break;
            }
        }


    }
    @Listener
    public void onPlayerAttackEntity(AttackEntityEvent event, @Root EntityDamageSource entityDamageSource) {

        Entity targetEntity = event.getTargetEntity();

        if(!(entityDamageSource.getSource() instanceof Player))
            return;

        Player playerSource = (Player) entityDamageSource.getSource();

        if(targetEntity instanceof Living) {
            FlagPath flagPath = new FlagPath(FlagKeys.ATTACK).add(getId(targetEntity));
            checkAndCancelIfNeeded(event, playerSource, playerSource.getLocation(), flagPath);
        } else {
            FlagPath flagPath = new FlagPath(FlagKeys.ENTITY_ATTACK).add(getId(targetEntity));
            checkAndCancelIfNeeded(event, playerSource, targetEntity.getLocation(), flagPath);
        }
    }

    private boolean onPlayerCollideEntity(CollideEntityEvent event, Player player, List<Entity> entities) {
        FlagPath flagPathPrefix = new FlagPath(FlagKeys.ENTITY_COLLIDE);
        for (Entity entity : entities) {
            if(entity.equals(player))
                continue;
            FlagPath currFlagPath = new FlagPath(flagPathPrefix).add(getId(entity));
            if(!checkAndCancelIfNeeded(event, player, entity.getLocation(), currFlagPath))
                return true;
        }
        return false;
    }

    @Listener
    public void onPlayerCollideEntityListener(CollideEntityEvent event) {

        List<Entity> entities = event.getEntities();
        Object source = event.getSource();
        if (source instanceof Entity)
            entities.add((Entity) source);

        for (Entity entity : entities) {
            if(entity instanceof Player) {
                if(onPlayerCollideEntity(event, (Player) entity, entities))
                    break;
            }
        }
    }

    @Listener
    public void onPlayerDropItem(DropItemEvent.Pre event, @First Player player) {
        FlagPath flagPathPrefix = new FlagPath(FlagKeys.ITEM_DROP);
        for (ItemStackSnapshot droppedItemStackSnapshot : event.getDroppedItems()) {
            FlagPath currFlagPath = new FlagPath(flagPathPrefix).add(getId(droppedItemStackSnapshot));
            if(!checkAndCancelIfNeeded(event, player, player.getLocation(), currFlagPath))
                return;
        }
    }
    @Listener
    public void onPlayerPickupItem(ChangeInventoryEvent.Pickup.Pre event, @First Player player) {
        FlagPath flagPathPrefix = new FlagPath(FlagKeys.ITEM_PICKUP);
        for (ItemStackSnapshot pickedupItemStackSnapshot : event.getFinal()) {
            FlagPath currFlagPath = new FlagPath(flagPathPrefix).add(getId(pickedupItemStackSnapshot));
            if(!checkAndCancelIfNeeded(event, player, event.getTargetEntity().getLocation(), currFlagPath))
                return;
        }
    }
    @Listener
    public void onPlayerInteractItem_Primary(InteractItemEvent.Primary event, @First Player player) {
        ItemStackSnapshot itemStackSnapshot = event.getItemStack();
        FlagPath flagPath = new FlagPath(FlagKeys.ITEM_INTERACT, FlagKeys.PRIMARY).add(getId(itemStackSnapshot));
        checkAndCancelIfNeeded(event, player, player.getLocation(), flagPath);
    }
    @Listener
    public void onPlayerInteractItem_Secondary(InteractItemEvent.Secondary event, @First Player player) {
        ItemStackSnapshot itemStackSnapshot = event.getItemStack();
        FlagPath flagPath = new FlagPath(FlagKeys.ITEM_INTERACT, FlagKeys.SECONDARY).add(getId(itemStackSnapshot));
        checkAndCancelIfNeeded(event, player, player.getLocation(), flagPath);
    }
    @Listener
    public void onPlayerUseItem(UseItemStackEvent.Start event, @First Player player) {
        ItemStackSnapshot itemStackSnapshot = event.getItemStackInUse();
        FlagPath flagPath = new FlagPath(FlagKeys.ITEM_USE).add(getId(itemStackSnapshot));
        checkAndCancelIfNeeded(event, player, player.getLocation(), flagPath);
    }


    /*@Listener
    public void test(ConstructEntityEvent.Pre event) {
        logger.info(event.getClass().getSimpleName() + ": " + event.getCause().toString());
    }
    @Listener
    public void test(ConstructEntityEvent.Post event) {
        logger.info(event.getClass().getSimpleName() + ": " + event.getCause().toString());
    }*/

    @Listener
    public void test(HarvestEntityEvent event, @First Player player) {
        logger.info(event.getClass().getSimpleName() + ": " + event.getCause().toString());
    }



    @Listener
    public void onPlayerSendCommand(SendCommandEvent event, @First Player player) {
        String command = event.getCommand();
        FlagPath flagPath = new FlagPath(FlagKeys.SEND_COMMAND).add(command);
        checkAndCancelIfNeeded(event, player, player.getLocation(), flagPath);
    }

    @Listener
    public void onPlayerTakeDamage(DamageEntityEvent event, @Root DamageSource source) {

        Entity targetEntity = event.getTargetEntity();
        if(!(event.getTargetEntity() instanceof Player))
            return;

        Player player = (Player) targetEntity;
        DamageType damageType = source.getType();
        FlagPath flagPath = new FlagPath(FlagKeys.NOT_TAKE_DAMAGE).add(getId(damageType));
        checkAndCancelIfNeeded(event, player, player.getLocation(), flagPath, true);
    }

    /*@Listener
    public void onPlayerAttack(DamageEntityEvent event, @Root EntityDamageSource entityDamageSource) {

        Entity targetEntity = event.getTargetEntity();

        if(!(entityDamageSource.getSource() instanceof Player))
            return;

        Player player = (Player) entityDamageSource.getSource();

        FlagPath flagPath = new FlagPath(Flags.ATTACK).add(getId(targetEntity));
        checkAndCancelIfNeeded(event, player, player.getLocation(), flagPath);
    }*/


}
