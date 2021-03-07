package me.doggy.justguard.command.region;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.config.Texts;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.MessageUtils;
import me.doggy.justguard.Pending;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class CommandClaim implements CommandExecutor
{

    public CommandResult execute(CommandSource src, CommandContext args)
    {
        Optional<String> nameOpt = args.getOne("name");

        if(!nameOpt.isPresent())
            return CommandResult.builder().successCount(0).build();

        String name = nameOpt.get();


        Region region = Pending.getRegion(src);
        if(region==null)
            MessageUtils.SendError(src, Text.of(TextManager.getText(Texts.ERR_NO_PENDING_REGION_FOUND)));
        else if(JustGuard.REGIONS.containsKey(name))
            MessageUtils.SendError(src, Text.of(TextManager.getText(Texts.ERR_REGION_ALREADY_EXISTS, name)));
        else if(Pending.uploadRegion(src, name))
            MessageUtils.Send(src, Text.of(TextManager.getText(Texts.CMD_ANSWER_REGION_CLAIMED, name)));
        else
            MessageUtils.SendError(src, Text.of(TextManager.getText(Texts.ERR_UNKNOWN)));

        return CommandResult.success();
    }
}