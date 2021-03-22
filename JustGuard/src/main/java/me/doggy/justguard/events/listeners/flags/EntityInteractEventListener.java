package me.doggy.justguard.events.listeners.flags;

import me.doggy.justguard.consts.FlagKeys;
import me.doggy.justguard.flag.FlagPath;
import me.doggy.justguard.utils.FlagUtils;
import me.doggy.justguard.utils.InventoryUtils;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.spawn.SpawnType;
import org.spongepowered.api.event.entity.*;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class EntityInteractEventListener {

    //
    // ENTITIES
    //
    @Listener
    public void onPlayerInteractEntity_Primary(InteractEntityEvent.Primary event, @First Player player) {
        Entity entity = event.getTargetEntity();
        FlagPath flagPath = FlagPath.of(FlagKeys.ENTITY, FlagKeys.INTERACT, FlagKeys.PRIMARY, FlagUtils.getPath(entity));
        FlagEventsHandler.handleEvent(event, player, entity.getLocation(), flagPath);
    }
    @Listener
    public void onPlayerInteractEntity_Secondary(InteractEntityEvent.Secondary event, @First Player player) {
        Entity entity = event.getTargetEntity();
        FlagPath flagPath = FlagPath.of(FlagKeys.ENTITY, FlagKeys.INTERACT, FlagKeys.SECONDARY, FlagUtils.getPath(entity));
        FlagEventsHandler.handleEvent(event, player, entity.getLocation(), flagPath);
    }
    @Listener
    public void onPlayerSpawnEntity(SpawnEntityEvent event, @First Player player) {
        Optional<SpawnType> spawnTypeOpt = event.getContext().get(EventContextKeys.SPAWN_TYPE);
        if(!spawnTypeOpt.isPresent())
            return;
        if(!event.getContext().get(EventContextKeys.PLAYER_PLACE).isPresent()) // if so - thats not a placement, this can be dropped item
            return;

        for (Entity entity : event.getEntities()) {
            FlagPath flagPath = FlagPath.of(FlagKeys.ENTITY, FlagKeys.SPAWN, FlagUtils.getPath(spawnTypeOpt.get()), FlagUtils.getPath(entity));
            if(FlagEventsHandler.handleEvent(event, player, entity.getLocation(), flagPath)) {
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

        FlagPath flagPath = FlagPath.of(FlagKeys.ENTITY, FlagKeys.ATTACK, FlagUtils.getPath(targetEntity));
        FlagEventsHandler.handleEvent(event, playerSource, playerSource.getLocation(), flagPath);
    }
    private boolean onPlayerCollideEntity(CollideEntityEvent event, Player player, Set<Entity> entities) {
        for (Entity entity : entities) {
            if(entity.equals(player))
                continue;
            FlagPath flagPath = FlagPath.of(FlagKeys.ENTITY, FlagKeys.COLLIDE, FlagUtils.getPath(entity));
            if(FlagEventsHandler.handleEvent(event, player, entity.getLocation(), flagPath))
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

        for (Entity entity : entities) {
            if(entity instanceof Player) {
                if(onPlayerCollideEntity(event, (Player) entity, entities))
                    break;
            }
        }
    }



    @Listener
    public void onPlayerTakeDamage(DamageEntityEvent event, @Root DamageSource source) {
        Entity targetEntity = event.getTargetEntity();
        if(!(event.getTargetEntity() instanceof Player))
            return;

        Player player = (Player) targetEntity;
        DamageType damageType = source.getType();
        FlagPath flagPath = FlagPath.of(FlagKeys.NOT_TAKE_DAMAGE, FlagUtils.getPath(damageType));
        FlagEventsHandler.handleEvent(event, player, player.getLocation(), flagPath, true);
    }

}
