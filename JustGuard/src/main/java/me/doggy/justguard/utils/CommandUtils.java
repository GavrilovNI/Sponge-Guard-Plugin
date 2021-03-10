package me.doggy.justguard.utils;

import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.consts.Texts;
import me.doggy.justguard.region.Region;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CommandUtils {

    public static boolean canContinueModifyRegion(Region region, CommandSource source, boolean sendError)
    {
        boolean result = true;
        Text error = Text.EMPTY;

        if(region == null) {
            result = false;
            error = Text.of(TextManager.getText(Texts.ERR_NO_REGION_FOUND));
        }
        if(!RegionUtils.canModify(region, source)) {
            result = false;
            error = Text.of(TextManager.getText(Texts.ERR_NOT_REGION_OWNER));
        }

        if(!result && sendError)
            MessageUtils.SendError(source, error);

        return result;
    }

    public static boolean cmdOnlyForPlayers(CommandSource source, boolean sendError)
    {
        boolean result = true;
        Text error = Text.EMPTY;

        if (!(source instanceof Player)) {
            result = false;
            error = Text.of(TextManager.getText(Texts.ERR_CMD_ONLY_FOR_PLAYERS));
        }

        if(!result && sendError)
            MessageUtils.SendError(source, error);

        return result;
    }
}
