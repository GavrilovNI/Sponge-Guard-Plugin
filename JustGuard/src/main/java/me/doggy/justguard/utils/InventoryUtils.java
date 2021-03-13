package me.doggy.justguard.utils;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.consts.Texts;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

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


    public static void dropItem(Location<World> location, ItemStack itemStack) {

        JustGuard.getInstance().getLogger().info("Qhere4");
        Entity itemStackEntity = location.createEntity(EntityTypes.ITEM);
        itemStackEntity.offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
        location.spawnEntity(itemStackEntity);
    }

    public static boolean addItemStackToInventoryImmediately(Player player, ItemStack itemStack) {

        InventoryTransactionResult inventoryTransactionResult = player.getInventory()
                .query(QueryOperationTypes.INVENTORY_TYPE.of(MainPlayerInventory.class))
                .offer(itemStack);

        for(ItemStackSnapshot rejectedItemStackSnapshot : inventoryTransactionResult.getRejectedItems()) {
            dropItem(player.getLocation().add(0, 0.25, 0), rejectedItemStackSnapshot.createStack());
        }

        return inventoryTransactionResult.getType().equals(InventoryTransactionResult.Type.SUCCESS);
    }
    public static void addItemStackToInventoryDelayed(Player player, ItemStack itemStack) {
        Task.builder()
                .execute(()->{
                    addItemStackToInventoryImmediately(player, itemStack);})
                .delay(0, TimeUnit.MICROSECONDS)
                .interval(0, TimeUnit.MICROSECONDS)
                .name("on SpawnEntityEvent canceled return item.")
                .submit(JustGuard.getInstance());
    }


    public static boolean isSelector(ItemStack itemStack)
    {
        Optional<Text> displayNameOpt = itemStack.get(Keys.DISPLAY_NAME);
        if(!displayNameOpt.isPresent())
            return false;

        return itemStack.getType().equals(SELECTOR_TYPE) && displayNameOpt.get().equals(GetSelectorDisplayName());
    }
}
