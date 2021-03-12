package me.doggy.justguard.events.player;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.consts.Flags;
import me.doggy.justguard.consts.Texts;
import me.doggy.justguard.flag.FlagPath;
import me.doggy.justguard.utils.FlagUtils;
import me.doggy.justguard.utils.MessageUtils;
import org.slf4j.Logger;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
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
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.function.Function;

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
            result.addInFront(Flags.TILE_ENTITIES);

        return result;
    }
    private FlagPath getId(Entity entity) {
        String lastId = entity.getType().getId();
        FlagPath result = new FlagPath(lastId);

        return result;
    }
    private FlagPath getId(ItemStackSnapshot itemStackSnapshot) {
        String lastId = itemStackSnapshot.getType().getId();
        FlagPath result = new FlagPath(lastId);
        return result;
    }
    private FlagPath getId(CatalogType catalogType) {
        String lastId = catalogType.getId().toLowerCase();
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
    public void onInteractBlockByPlayer_Primary(InteractBlockEvent.Primary event, @First Player player) {
        BlockSnapshot blockSnapshot = event.getTargetBlock();
        FlagPath flagPath = new FlagPath(Flags.BLOCK_INTERACT, Flags.PRIMARY).add(getId(blockSnapshot));
        checkAndCancelIfNeeded(event, player, blockSnapshot.getLocation(), flagPath);
    }
    @Listener
    public void onInteractBlockByPlayer_Secondary(InteractBlockEvent.Secondary event, @First Player player) {
        BlockSnapshot blockSnapshot = event.getTargetBlock();
        FlagPath flagPath = new FlagPath(Flags.BLOCK_INTERACT, Flags.SECONDARY).add(getId(blockSnapshot));
        checkAndCancelIfNeeded(event, player, blockSnapshot.getLocation(), flagPath);
    }

    @Listener
    public void onInteractInventoryByPlayer_Open(InteractInventoryEvent.Open event, @First Player player) {
        EventContext context = event.getContext();
        Optional<BlockSnapshot> blockHitOpt = context.get(EventContextKeys.BLOCK_HIT);
        if(!blockHitOpt.isPresent())
            return;

        BlockSnapshot blockSnapshot = blockHitOpt.get();
        FlagPath flagPath = new FlagPath(Flags.INVENTORY_INTERACT, Flags.OPEN).add(getId(blockSnapshot));
        checkAndCancelIfNeeded(event, player, blockSnapshot.getLocation(), flagPath);
    }

    @Listener
    public void onChangeBlockByPlayer_Break(ChangeBlockEvent.Break event, @First Player player) {
        checkAndCancelIfNeeded(event, player, t->(BlockSnapshot) t.getFinal(), new FlagPath(Flags.BLOCK_BREAK));
    }
    @Listener
    public void onChangeBlockByPlayer_Place(ChangeBlockEvent.Place event, @First Player player) {
        checkAndCancelIfNeeded(event, player, t->(BlockSnapshot) t.getFinal(), new FlagPath(Flags.BLOCK_PLACE));
    }

    @Listener
    public void onInteractEntityByPlayer_Primary(InteractEntityEvent.Primary event, @First Player player) {
        Entity targetEntity = event.getTargetEntity();
        FlagPath flagPath = new FlagPath(Flags.ENTITY_INTERACT, Flags.PRIMARY).add(getId(targetEntity));
        checkAndCancelIfNeeded(event, player, targetEntity.getLocation(), flagPath);

        logger.info("tileentity: " + String.valueOf(targetEntity instanceof TileEntity));
    }
    @Listener
    public void onInteractEntityByPlayer_Secondary(InteractEntityEvent.Secondary event, @First Player player) {
        Entity targetEntity = event.getTargetEntity();
        FlagPath flagPath = new FlagPath(Flags.ENTITY_INTERACT, Flags.SECONDARY).add(getId(targetEntity));
        checkAndCancelIfNeeded(event, player, targetEntity.getLocation(), flagPath);

        logger.info("tileentity: " + String.valueOf(targetEntity instanceof TileEntity));
    }
    @Listener
    public void onSpawnEntityByPlayer(SpawnEntityEvent event, @First Player player) {
        Optional<SpawnType> spawnTypeOpt = event.getContext().get(EventContextKeys.SPAWN_TYPE);
        if(!spawnTypeOpt.isPresent())
            return;

        List<Entity> entities = event.getEntities();
        for (Entity targetEntity : entities) {
            FlagPath flagPath = new FlagPath(Flags.ENTITY_SPAWN).add(getId(spawnTypeOpt.get())).add(getId(targetEntity));
            if(!checkAndCancelIfNeeded(event, player, targetEntity.getLocation(), flagPath))
                return;
        }
    }

    @Listener
    public void onPlayerAttackEntity(AttackEntityEvent event, @Root EntityDamageSource entityDamageSource) {

        logger.info("onPlayerAttackEntity");
        logger.info(event.getContext().toString());

        Entity targetEntity = event.getTargetEntity();

        if(!(entityDamageSource.getSource() instanceof Player))
            return;

        Player playerSource = (Player) entityDamageSource.getSource();

        if(targetEntity instanceof Living) {
            FlagPath flagPath = new FlagPath(Flags.ATTACK).add(getId(targetEntity));
            checkAndCancelIfNeeded(event, playerSource, playerSource.getLocation(), flagPath);
        } else {
            FlagPath flagPath = new FlagPath(Flags.ENTITY_ATTACK).add(getId(targetEntity));
            checkAndCancelIfNeeded(event, playerSource, targetEntity.getLocation(), flagPath);
        }
    }
    @Listener
    public void test(DropItemEvent event, @First Player player) {
        logger.info("DropItemEvent");
        logger.info(event.getContext().toString());
    }

    @Listener
    public void test(HarvestEntityEvent event, @First Player player) {
        logger.info("HarvestEntityEvent");
    }
    @Listener
    public void test(CollideEntityEvent event, @First Player player) {
        logger.info("CollideEntityEvent");
    }


    @Listener
    public void onInteractItemByPlayer_Primary(InteractItemEvent.Primary event, @First Player player) {
        ItemStackSnapshot itemStackSnapshot = event.getItemStack();
        FlagPath flagPath = new FlagPath(Flags.ITEM_INTERACT, Flags.PRIMARY).add(getId(itemStackSnapshot));
        checkAndCancelIfNeeded(event, player, player.getLocation(), flagPath);
    }
    @Listener
    public void onInteractItemByPlayer_Secondary(InteractItemEvent.Secondary event, @First Player player) {
        ItemStackSnapshot itemStackSnapshot = event.getItemStack();
        FlagPath flagPath = new FlagPath(Flags.ITEM_INTERACT, Flags.SECONDARY).add(getId(itemStackSnapshot));
        checkAndCancelIfNeeded(event, player, player.getLocation(), flagPath);
    }



    @Listener
    public void onPlayerSendCommand(SendCommandEvent event, @First Player player) {
        String command = event.getCommand();
        FlagPath flagPath = new FlagPath(Flags.SEND_COMMAND).add(command);
        checkAndCancelIfNeeded(event, player, player.getLocation(), flagPath);
    }

    @Listener
    public void onPlayerTakeDamage(DamageEntityEvent event, @Root DamageSource source) {

        Entity targetEntity = event.getTargetEntity();
        if(!(event.getTargetEntity() instanceof Player))
            return;

        Player player = (Player) targetEntity;
        DamageType damageType = source.getType();
        FlagPath flagPath = new FlagPath(Flags.NOT_TAKE_DAMAGE).add(getId(damageType));
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
