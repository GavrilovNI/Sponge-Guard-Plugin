package me.doggy.justguard.command.region.modify;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.command.CommandsRegistrator;
import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.consts.Texts;
import me.doggy.justguard.flag.FlagPath;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.CommandUtils;
import me.doggy.justguard.utils.FlagUtils;
import me.doggy.justguard.utils.MessageUtils;
import me.doggy.justguard.flag.FlagValue;
import ninja.leaping.configurate.ConfigurationNode;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class CommandSetFlag implements CommandExecutor
{
    public static final Logger logger = JustGuard.getInstance().getLogger();

    public CommandResult execute(CommandSource src, CommandContext args)
    {
        Optional<String> regionIdOpt = args.getOne(CommandsRegistrator.REGION_ID);
        Optional<String> flagOpt = args.getOne("flag");
        Optional<String> valueOpt = args.getOne("value");


        if(!regionIdOpt.isPresent() || !flagOpt.isPresent() || !valueOpt.isPresent())
            return CommandResult.builder().successCount(0).build();

        String regionId = regionIdOpt.get();
        String flag = flagOpt.get();
        FlagValue newValue = FlagValue.parse(valueOpt.get());

        Region region = JustGuard.REGIONS.get(regionId);

        if(!CommandUtils.canContinueModifyRegion(region, src, true))
            return CommandResult.success();

        FlagPath flagPath = FlagPath.parse(flag);
        if(flagPath.isEmpty()) {
            MessageUtils.SendError(src, Text.of(TextManager.getText(Texts.ERR_CMD_NOT_ENOUGH_ARGUMENTS)));
            return CommandResult.success();
        }

        FlagValue oldValue = region.setFlag(flagPath, newValue);

        MessageUtils.Send(src, Text.of(TextManager.getText(
                Texts.CMD_ANSWER_FLAG_CHANGED,
                regionId,
                flagPath.getFullPath(),
                oldValue.getValueToString(),
                newValue.getValueToString()
        )));


        return CommandResult.success();
    }
}