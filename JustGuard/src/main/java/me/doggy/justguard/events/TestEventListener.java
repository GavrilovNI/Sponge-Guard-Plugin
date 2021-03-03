package me.doggy.justguard.events;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.ConfigManager;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityArchetype;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;

import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.world.LocatableBlock;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;


public class TestEventListener {

    static Logger logger = JustGuard.getInstance().getLogger();
    static ConfigManager configManager = JustGuard.getInstance().getConfigManager();

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void a0(ChangeBlockEvent.Pre event, @First Player player)
    {
        logger.info("ChangeBlockEvent.Pre");
        logger.info(event.getCause().toString());

        if(!configManager.getFlag("ChangeBlockEventPre")) {
            event.setCancelled(true);
        }


        logger.info("");
        logger.info("");
        logger.info("");
        logger.info("");

    }

    private  void printEntityInfo(Entity entity)
    {
        EntityType type = entity.getType();

        logger.info("Entity: " + entity.toString());
        logger.info("EntityArchetype: " + EntityArchetype.of(type).toString());
        logger.info("EntityType: " + entity.getType().toString());
        logger.info("EntityClass: " + entity.getClass().toString());
    }

    @Listener
    public void a1(InteractEntityEvent.Primary event, @First Player player)
    {
        Entity targetEntity = event.getTargetEntity();
        EntityType type = targetEntity.getType();

        logger.info("InteractEntityEvent.Primary");
        printEntityInfo(targetEntity);
        logger.info(event.getCause().toString());


        if(!configManager.getFlag("InteractEntityEventPrimary")) {
            event.setCancelled(true);
        }

        logger.info("");
        logger.info("");
        logger.info("");
        logger.info("");
    }

    @Listener
    public void a2(InteractEntityEvent.Secondary event, @First Player player)
    {

        Entity targetEntity = event.getTargetEntity();
        EntityType type = targetEntity.getType();

        logger.info("InteractEntityEvent.Secondary");
        printEntityInfo(targetEntity);
        logger.info(event.getCause().toString());


        if(!configManager.getFlag("InteractEntityEventSecondary")) {
            event.setCancelled(true);
        }

        logger.info("");
        logger.info("");
        logger.info("");
        logger.info("");
    }

    @Listener
    public void a3(SpawnEntityEvent event, @First Player player) {

        logger.info("SpawnEntityEvent");
        logger.info(event.getCause().toString());


        if(!configManager.getFlag("SpawnEntityEvent")) {
            event.setCancelled(true);
        }

        logger.info("");
        logger.info("");
        logger.info("");
        logger.info("");
    }



    /*@Listener
    public void test(UseItemStackEvent event, @First Player player) {

        Logger logger = JustGuard.getInstance().getLogger();
        ConfigManager configManager = JustGuard.getInstance().getConfigManager();

        logger.info("UseItemStackEvent by player");

        logger.info(event.getCause().toString());

        logger.info("");
        logger.info("");
        logger.info("");
        logger.info("");

    }

    @Listener
    public void test2(InteractItemEvent event, @First Player player) {



        Logger logger = JustGuard.getInstance().getLogger();
        ConfigManager configManager = JustGuard.getInstance().getConfigManager();

        logger.info("InteractItemEvent by player");

        logger.info(event.getCause().toString());

        logger.info("");
        logger.info("");
        logger.info("");
        logger.info("");

    }

    @Listener
    public void onBucketUse(InteractItemEvent.Secondary event, @First Player player) {


        Logger logger = JustGuard.getInstance().getLogger();
        ConfigManager configManager = JustGuard.getInstance().getConfigManager();

        logger.info("InteractItemEvent.Secondary by player");

        logger.info(event.getCause().toString());
        logger.info(event.getContext().toString());

        Optional<SpawnType> spawnTypeOpt = event.getContext().get(EventContextKeys.SPAWN_TYPE);

        if(spawnTypeOpt.isPresent())
        {
            SpawnType spawnType = spawnTypeOpt.get();
            //spawnType.equals(SpawnTypes.)
        }

        if(!configManager.getFlag("useBucketForPlace"))
        {
            Optional<ItemStackSnapshot> usedItemOpt = event.getContext().get(EventContextKeys.USED_ITEM);
            if(usedItemOpt.isPresent())
            {
                ItemStackSnapshot usedItem = usedItemOpt.get();

                ItemType usedItemType = usedItem.getType();

                ItemStack itemStack = usedItem.createStack();

                logger.info(itemStack.getContainers().toString());
                logger.info(itemStack.getKeys().toString());



                Optional<ItemType> forgeBucketFilled = Sponge.getRegistry().getType(ItemType.class, "forge:bucketfilled");


                logger.info(forgeBucketFilled.orElse(ItemTypes.AIR).toString());

                if(usedItemType.equals(ItemTypes.WATER_BUCKET) ||
                        usedItemType.equals(ItemTypes.LAVA_BUCKET) ||
                        (forgeBucketFilled.isPresent()) && usedItemType.equals(forgeBucketFilled.get()))
                {
                    event.setCancelled(true);
                }
            }

        }


        if(!configManager.getFlag("interactItem"))
            event.setCancelled(true);

        logger.info("");
        logger.info("");
        logger.info("");
        logger.info("");

    }


    @Listener
    public void onBlockPlaced(ChangeBlockEvent.Place event)
    {
        Logger logger = JustGuard.getInstance().getLogger();
        ConfigManager configManager = JustGuard.getInstance().getConfigManager();

        Player player = (Player)event.getCause().first(Player.class).orElse(null);

        if(player != null)
        {
            logger.info("ChangeBlockEvent.Place by player");

            logger.info(event.getContext().toString());

            List<Transaction<BlockSnapshot>> transactions = event.getTransactions();

            logger.info("event.getTransactions() size: "+transactions.size());

            for (int i = 0; i < transactions.size(); i++)
            {
                Transaction<BlockSnapshot> transaction = transactions.get(i);
                logger.info("tr "+i+": "+transaction.toString());


                BlockSnapshot blockSnapshot = transaction.getFinal();
                logger.info("blockSnapshot: "+blockSnapshot.toString());
            }

            if(!configManager.getFlag("placeBlock"))
                event.setCancelled(true);

            logger.info("");
            logger.info("");
            logger.info("");
            logger.info("");
        }


    }



    @Listener
    public void onEntityPlacedByPlayer(SpawnEntityEvent event, @First Player player) {

        Logger logger = JustGuard.getInstance().getLogger();
        ConfigManager configManager = JustGuard.getInstance().getConfigManager();

        logger.info("SpawnEntityEvent by player");

        Map<EventContextKey<?>, Object> context = event.getContext().asMap();

        {
            int i = 0;
            for (Map.Entry<EventContextKey<?>, Object> entry : context.entrySet()) {
                logger.info(i++ + ":k: " + entry.getKey());
                logger.info(i++ + ":v: " + entry.getValue());

            }
        }

        if(!configManager.getFlag("spawnEntity"))
        {

            Optional<ItemStackSnapshot> usedItemOpt = event.getContext().get(EventContextKeys.USED_ITEM);
            if(usedItemOpt.isPresent())
            {
                event.setCancelled(true);

                if(player.gameMode().exists() && !player.gameMode().get().equals(GameModes.CREATIVE))
                {
                    ItemStackSnapshot usedItem = usedItemOpt.get();

                    ItemStack itemStackToReturn = usedItem.createStack();
                    itemStackToReturn.setQuantity(1);
                    Utils.addItemStackToInventory(player,itemStackToReturn);

                }
            }

        }

        List<Entity> entities = event.getEntities();

        logger.info("event.getEntities() size: "+entities.size());

        for (int i = 0; i < entities.size(); i++)
        {
            Entity entity = entities.get(i);
            logger.info("tr "+i+": "+entity.toString());
            logger.info("EntityType: "+entity.getType().toString());
        }

        logger.info("");
        logger.info("");
        logger.info("");
        logger.info("");
    }*/


}
