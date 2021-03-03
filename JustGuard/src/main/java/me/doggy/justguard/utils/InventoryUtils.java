package me.doggy.justguard.utils;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.world.World;

public class InventoryUtils {

    public static void dropItem(World world, Vector3d location, ItemStack itemStack) {
        Entity itemStackEntity = world.createEntity(EntityTypes.ITEM, location);
        itemStackEntity.offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
        world.spawnEntity(itemStackEntity);
    }

    public static boolean addItemStackToInventory(Player player, ItemStack itemStack) {

        boolean gotItem = player.getInventory()
                .query(QueryOperationTypes.INVENTORY_TYPE.of(MainPlayerInventory.class))
                .offer(itemStack).getType().equals(InventoryTransactionResult.Type.SUCCESS);

        if(gotItem)
        {
            return true;
        }
        else
        {
            dropItem(player.getWorld(), player.getPosition().add(0F, 0.25F, 0F), itemStack);
            return false;
        }
    }


    public static boolean isSelector(ItemStack itemStack)
    {
        return itemStack.getType().equals(ItemTypes.WOODEN_AXE);
    }
}
