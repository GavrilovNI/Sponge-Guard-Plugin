package me.doggy.justguard.command.region.pending;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.Pending;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.consts.Texts;
import me.doggy.justguard.utils.MessageUtils;
import me.doggy.justguard.utils.help.PendingRegion;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class CommandExpandVert implements CommandExecutor
{
    Logger logger = JustGuard.getInstance().getLogger();

    public CommandResult execute(CommandSource source, CommandContext args)
    {
        PendingRegion region = Pending.getRegion(source);
        if(region==null) {
            MessageUtils.sendError(source, Text.of(TextManager.getText(Texts.ERR_NO_PENDING_REGION_FOUND)));
            return CommandResult.success();
        }

        region.aabbBuilder.expandVert();
        MessageUtils.send(source, Text.of(TextManager.getText(Texts.CMD_ANSWER_BOUNDS_EXPANDED_VERT)));

        return CommandResult.success();
    }
}