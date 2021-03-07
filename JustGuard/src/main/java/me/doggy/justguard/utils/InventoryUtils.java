package me.doggy.justguard.utils;

import com.flowpowered.math.vector.Vector3d;
import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.config.Texts;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class InventoryUtils {

    private static final ItemType SELECTOR_TYPE = ItemTypes.WOODEN_AXE;

    private static Text GetSelectorDisplayName()
    {
        return Text.of(TextManager.getText(Texts.SELECTOR));
    }
    public static ItemStack GetSelector() {
        ItemStack result = ItemStack.of(SELECTOR_TYPE);
        result.offer(Keys.DISPLAY_NAME, GetSelectorDisplayName());
        return result;
    }


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
        Optional<Text> displayNameOpt = itemStack.get(Keys.DISPLAY_NAME);
        if(!displayNameOpt.isPresent())
            return false;

        return itemStack.getType().equals(SELECTOR_TYPE) && displayNameOpt.get().equals(GetSelectorDisplayName());
    }
}
