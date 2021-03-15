package me.doggy.justguard.events.listeners.flags;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.RegionsHolder;
import me.doggy.justguard.consts.FlagKeys;
import me.doggy.justguard.events.PlayerEnterRegionEvent;
import me.doggy.justguard.events.PlayerExitRegionEvent;
import me.doggy.justguard.flag.FlagPath;
import me.doggy.justguard.flag.FlagValue;
import me.doggy.justguard.flag.Flags;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.FlagUtils;
import me.doggy.justguard.utils.InventoryUtils;
import me.doggy.justguard.utils.MessageUtils;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.vehicle.Boat;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.Cause;
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
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

public class PlayerFlagsEventListener {

    private static final Logger logger = JustGuard.getInstance().getLogger();

    private boolean isTileEntity(BlockSnapshot blockSnapshot) {
        Optional<Location<World>> locationOpt = blockSnapshot.getLocation();
        if(!locationOpt.isPresent()) {
            return blockSnapshot.createArchetype().isPresent();
        }
        return locationOpt.get().getTileEntity().isPresent();
    }
    private FlagPath getId(BlockState blockState) {
        String lastId = blockState.getType().getId();
        FlagPath result = FlagPath.of(lastId);
        return result;
    }
    private FlagPath getId(BlockSnapshot blockSnapshot) {
        FlagPath.Builder builder = FlagPath.builder().add(getId(blockSnapshot.getState()));
        if(isTileEntity(blockSnapshot))
            builder.addFront(FlagKeys.TILE_ENTITIES);
        return builder.build();
    }
    private FlagPath getId(Entity entity) {
        String lastId = entity.getType().getId();
        FlagPath result = FlagPath.of(lastId);
        return result;
    }
    private FlagPath getId(EntitySnapshot entitySnapshot) {
        String lastId = entitySnapshot.getType().getId();
        FlagPath result = FlagPath.of(lastId);
        return result;
    }
    private FlagPath getId(ItemStackSnapshot itemStackSnapshot) {
        String lastId = itemStackSnapshot.getType().getId();
        FlagPath result = FlagPath.of(lastId);
        return result;
    }
    private FlagPath getId(SpawnType spawnType) {
        String lastId = spawnType.getId().toLowerCase();
        FlagPath result = FlagPath.of(lastId);
        return result;
    }
    private FlagPath getId(DamageType damageType) {
        String lastId = damageType.getId().toLowerCase();
        FlagPath result = FlagPath.of(lastId);
        return result;
    }

    private boolean handleEvent(Cancellable event, Player player, Location<World> location, FlagPath flagPath) {
        if(!FlagUtils.hasPlayerFlagAccess(player, location, flagPath)) {
            event.setCancelled(true);
            MessageUtils.sendErrorNoFlagAccess(player, flagPath);
            return true;
        }
        return false;
    }
    private boolean handleEvent(Cancellable event, Player player, Optional<Location<World>> locationOpt, FlagPath flagPath) {
        if(!locationOpt.isPresent())
            return false;
        return handleEvent(event, player, locationOpt.get(), flagPath);
    }
    private boolean handleEvent(Cancellable event, Player player, Location<World> location, FlagPath flagPath, boolean inverted) {
        if(FlagUtils.hasPlayerFlagAccess(player, location, flagPath) == inverted) {
            event.setCancelled(true);
            MessageUtils.sendErrorNoFlagAccess(player, flagPath);
            return true;
        }
        return false;
    }
    private boolean handleEvent(Cancellable event, Player player, Optional<Location<World>> locationOpt, FlagPath flagPath, boolean inverted) {
        if(!locationOpt.isPresent())
            return false;
        return handleEvent(event, player, locationOpt.get(), flagPath, inverted);
    }


    //
    // BLOCKS
    //
    @Listener
    public void onPlayerInteractBlock_Primary(InteractBlockEvent.Primary event, @First Player player) {
        BlockSnapshot blockSnapshot = event.getTargetBlock();
        FlagPath flagPath = FlagPath.of(FlagKeys.BLOCK_INTERACT, FlagKeys.PRIMARY, getId(blockSnapshot));
        handleEvent(event, player, blockSnapshot.getLocation(), flagPath);
    }
    @Listener
    public void onPlayerInteractBlock_Secondary(InteractBlockEvent.Secondary event, @First Player player) {
        BlockSnapshot blockSnapshot = event.getTargetBlock();
        FlagPath flagPath = FlagPath.of(FlagKeys.BLOCK_INTERACT, FlagKeys.SECONDARY, getId(blockSnapshot));
        handleEvent(event, player, blockSnapshot.getLocation(), flagPath);    }
    @Listener
    public void onPlayerCollideBlock(CollideBlockEvent event, @First Player player) {
        BlockState blockState = event.getTargetBlock();
        FlagPath flagPath = FlagPath.of(FlagKeys.BLOCK_COLLIDE, getId(blockState));
        handleEvent(event, player, event.getTargetLocation(), flagPath);
    }
    @Listener
    public void onPlayerBreakBlock(ChangeBlockEvent.Break event, @First Player player) {
        for(Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            BlockSnapshot blockSnapshot = transaction.getOriginal();
            FlagPath flagPath = FlagPath.of(FlagKeys.BLOCK_BREAK, getId(blockSnapshot));
            if(handleEvent(event, player, blockSnapshot.getLocation(), flagPath))
                return;
        }
    }
    @Listener
    public void onPlayerPlaceBlock(ChangeBlockEvent.Place event, @First Player player) {
        for(Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            BlockSnapshot blockSnapshot = transaction.getFinal();
            FlagPath flagPath = FlagPath.of(FlagKeys.BLOCK_PLACE, getId(blockSnapshot));
            if(handleEvent(event, player, blockSnapshot.getLocation(), flagPath))
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
        FlagPath flagPath = FlagPath.of(FlagKeys.INVENTORY_INTERACT, FlagKeys.OPEN, getId(blockSnapshot));
        handleEvent(event, player, blockSnapshot.getLocation(), flagPath);
    }


    //
    // ENTITIES
    //
    @Listener
    public void onPlayerInteractEntity_Primary(InteractEntityEvent.Primary event, @First Player player) {
        Entity entity = event.getTargetEntity();
        FlagPath flagPath = FlagPath.of(FlagKeys.ENTITY_INTERACT, FlagKeys.PRIMARY, getId(entity));
        handleEvent(event, player, entity.getLocation(), flagPath);
    }
    @Listener
    public void onPlayerInteractEntity_Secondary(InteractEntityEvent.Secondary event, @First Player player) {
        Entity entity = event.getTargetEntity();
        FlagPath flagPath = FlagPath.of(FlagKeys.ENTITY_INTERACT, FlagKeys.SECONDARY, getId(entity));
        handleEvent(event, player, entity.getLocation(), flagPath);
    }
    @Listener
    public void onPlayerSpawnEntity(SpawnEntityEvent event, @First Player player) {
        Optional<SpawnType> spawnTypeOpt = event.getContext().get(EventContextKeys.SPAWN_TYPE);
        if(!spawnTypeOpt.isPresent())
            return;
        if(!event.getContext().get(EventContextKeys.PLAYER_PLACE).isPresent()) // if so - thats not a placement, this can be dropped item
            return;

        for (Entity entity : event.getEntities()) {
            FlagPath flagPath = FlagPath.of(FlagKeys.ENTITY_SPAWN, getId(spawnTypeOpt.get()), getId(entity));
            if(handleEvent(event, player, entity.getLocation(), flagPath)) {
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
            FlagPath flagPath = FlagPath.of(FlagKeys.ATTACK, getId(targetEntity));
            handleEvent(event, playerSource, playerSource.getLocation(), flagPath);
        } else {
            FlagPath flagPath = FlagPath.of(FlagKeys.ENTITY_ATTACK, getId(targetEntity));
            handleEvent(event, playerSource, targetEntity.getLocation(), flagPath);
        }
    }
    private boolean onPlayerCollideEntity(CollideEntityEvent event, Player player, Set<Entity> entities) {
        for (Entity entity : entities) {
            if(entity.equals(player))
                continue;
            FlagPath flagPath = FlagPath.of(FlagKeys.ENTITY_COLLIDE, getId(entity));
            if(handleEvent(event, player, entity.getLocation(), flagPath))
                return true;
        }
        return false;
    }
    @Listener
    public void onPlayerCollideEntityListener(CollideEntityEvent event) {

        Set<Entity> entities = new HashSet<>(event.getEntities());
        Object source = event.getSource();
        if (source instanceof Entity)
            entities.add((Entity) source);
        //for (Entity entity : entities)
        //    entities.addAll(entity.getPassengers());

        if(entities.stream().anyMatch(x->x instanceof Player || x instanceof Boat))
            logger.info(entities.toString());

        for (Entity entity : entities) {
            if(entity instanceof Player) {
                if(onPlayerCollideEntity(event, (Player) entity, entities))
                    break;
            }
        }
    }



    //
    // ITEMS
    //
    @Listener
    public void onPlayerInteractItem_Primary(InteractItemEvent.Primary event, @First Player player) {
        ItemStackSnapshot itemStackSnapshot = event.getItemStack();
        FlagPath flagPath = FlagPath.of(FlagKeys.ITEM_INTERACT, FlagKeys.PRIMARY, getId(itemStackSnapshot));
        handleEvent(event, player, player.getLocation(), flagPath);
    }
    @Listener
    public void onPlayerInteractItem_Secondary(InteractItemEvent.Secondary event, @First Player player) {
        ItemStackSnapshot itemStackSnapshot = event.getItemStack();
        FlagPath flagPath = FlagPath.of(FlagKeys.ITEM_INTERACT, FlagKeys.SECONDARY, getId(itemStackSnapshot));
        handleEvent(event, player, player.getLocation(), flagPath);
    }
    @Listener
    public void onPlayerDropItem(DropItemEvent.Pre event, @First Player player) {
        for (ItemStackSnapshot itemStackSnapshot : event.getDroppedItems()) {
            FlagPath flagPath = FlagPath.of(FlagKeys.ITEM_DROP, getId(itemStackSnapshot));
            if(handleEvent(event, player, player.getLocation(), flagPath))
                return;
        }
    }
    @Listener
    public void onPlayerPickupItem(ChangeInventoryEvent.Pickup.Pre event, @First Player player) {
        for (ItemStackSnapshot itemStackSnapshot : event.getFinal()) {
            FlagPath flagPath = FlagPath.of(FlagKeys.ITEM_PICKUP, getId(itemStackSnapshot));
            if(handleEvent(event, player, player.getLocation(), flagPath))
                return;
        }
    }
    @Listener
    public void onPlayerUseItem(UseItemStackEvent.Start event, @First Player player) {
        ItemStackSnapshot itemStackSnapshot = event.getItemStackInUse();
        FlagPath flagPath = FlagPath.of(FlagKeys.ITEM_USE, getId(itemStackSnapshot));
        handleEvent(event, player, player.getLocation(), flagPath);
    }


    //
    // MOVING
    //
    public boolean canPlayerMove(Player player, Map<String, Region> regionsFrom, Map<String, Region> regionsTo, FlagPath innerFlag) {
        FlagPath flagPath = FlagPath.of(FlagKeys.EXIT, innerFlag);

        if(FlagUtils.hasPlayerFlagAccess(player, regionsFrom, flagPath)) {
            flagPath = FlagPath.of(FlagKeys.ENTER, innerFlag);

            if(FlagUtils.hasPlayerFlagAccess(player, regionsTo, flagPath)) {

                boolean canceled = false;
                for (Map.Entry<String, Region> regionEntry : regionsTo.entrySet()) {
                    PlayerEnterRegionEvent event = new PlayerEnterRegionEvent(player, regionEntry.getValue());
                    event.setCancelled(canceled);
                    Sponge.getEventManager().post(event);
                    if(event.isCancelled())
                        canceled = true;
                }
                for (Map.Entry<String, Region> regionEntry : regionsFrom.entrySet()) {
                    PlayerExitRegionEvent event = new PlayerExitRegionEvent(player, regionEntry.getValue());
                    event.setCancelled(canceled);
                    Sponge.getEventManager().post(event);
                    if(event.isCancelled())
                        canceled = true;
                }

                return !canceled;
            }
        }
        MessageUtils.sendErrorNoFlagAccess(player, flagPath);
        return false;
    }

    public void onEntityMove(MoveEntityEvent event, FlagPath innerFlag) {

        Location<World> fromLocation = event.getFromTransform().getLocation();
        Location<World> toLocation = event.getToTransform().getLocation();

        Map<String, Region> regionsFrom = RegionsHolder.getRegions(x->x.getValue().contains(fromLocation));
        Map<String, Region> regionsTo = RegionsHolder.getRegions(x->x.getValue().contains(toLocation));

        Set<String> keysToRemove = new HashSet<>(regionsFrom.keySet());
        keysToRemove.retainAll(regionsTo.keySet());
        regionsFrom.keySet().removeAll(keysToRemove);
        regionsTo.keySet().removeAll(keysToRemove);

        //moving on boats not working properly

        Entity targetEntity = event.getTargetEntity();
        if(targetEntity instanceof Player) {
            if(!canPlayerMove((Player) targetEntity, regionsFrom, regionsTo, innerFlag)) {
                event.setCancelled(true);
                return;
            }
        }
        List<Entity> passengers = targetEntity.getPassengers();
        for (Entity passenger : passengers) {
            if(passenger instanceof Player) {
                if (!canPlayerMove((Player) passenger, regionsFrom, regionsTo, innerFlag)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @Listener
    public void onEntityMove(MoveEntityEvent event, @First Player player) {

        FlagPath flagPath;
        if(event instanceof MoveEntityEvent.Teleport) {
            FlagPath.Builder builder = FlagPath.builder().add(FlagKeys.TELEPORT);
            if (event instanceof MoveEntityEvent.Teleport.Portal)
                builder.add(FlagKeys.PORTAL);
            else
                builder.add(FlagKeys.BASE);
            flagPath = builder.build();
        } else {
            flagPath = FlagPath.of(FlagKeys.BASE);
        }
        onEntityMove(event, flagPath);
    }

    @Listener
    public void onPlayerEnterRegion(PlayerEnterRegionEvent event) {
        logger.info(event.getClass().getSimpleName()+": "+event.getCause());
        FlagPath textEnterPath = FlagPath.of(FlagKeys.MESSAGES, FlagKeys.ENTER);
        FlagValue enterMessage = event.getRegion().getFlag(textEnterPath);
        if(!enterMessage.isEmpty())
            MessageUtils.send(event.getTargetEntity(), Text.of(enterMessage.getString("")));
    }
    @Listener
    public void onPlayerExitRegion(PlayerExitRegionEvent event) {
        logger.info(event.getClass().getSimpleName()+": "+event.getCause());
        FlagPath textEnterPath = FlagPath.of(FlagKeys.MESSAGES, FlagKeys.EXIT);
        FlagValue enterMessage = event.getRegion().getFlag(textEnterPath);
        if(!enterMessage.isEmpty())
            MessageUtils.send(event.getTargetEntity(), Text.of(enterMessage.getString("")));
    }


    //
    // Player specific
    //
    @Listener
    public void onPlayerSendCommand(SendCommandEvent event, @First Player player) {
        String command = event.getCommand()+FlagPath.FLAG_SPLITTER+event.getArguments().replace(" ",FlagPath.FLAG_SPLITTER);
        FlagPath flagPath = FlagPath.of(FlagKeys.SEND_COMMAND, command);
        handleEvent(event, player, player.getLocation(), flagPath);
    }
    @Listener
    public void onPlayerTakeDamage(DamageEntityEvent event, @Root DamageSource source) {
        Entity targetEntity = event.getTargetEntity();
        if(!(event.getTargetEntity() instanceof Player))
            return;

        Player player = (Player) targetEntity;
        DamageType damageType = source.getType();
        FlagPath flagPath = FlagPath.of(FlagKeys.NOT_TAKE_DAMAGE, getId(damageType));
        handleEvent(event, player, player.getLocation(), flagPath, true);
    }
}
