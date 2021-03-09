package me.doggy.justguard.command;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.config.Texts;
import me.doggy.justguard.utils.InventoryUtils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

public class CommandWand implements CommandExecutor
{

    public CommandResult execute(CommandSource src, CommandContext args)
    {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of(TextManager.getText(Texts.ERR_CMD_ONLY_FOR_PLAYERS)));
        }

        Player player = (Player) src;
        InventoryUtils.addItemStackToInventory(player, InventoryUtils.GetSelector());
        src.sendMessage(Text.of(TextManager.getText(Texts.CMD_ANSWER_WAND)));

        return CommandResult.success();
    }
}