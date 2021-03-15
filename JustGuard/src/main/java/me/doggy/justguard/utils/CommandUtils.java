package me.doggy.justguard.utils;

import me.doggy.justguard.config.TextManager;
import me.doggy.justguard.consts.Permissions;
import me.doggy.justguard.consts.Texts;
import me.doggy.justguard.region.Region;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CommandUtils {

    public static boolean isRegionFound(CommandSource source, Region region, String regionId) {
        if(region == null) {
            MessageUtils.sendError(source, Text.of(TextManager.getText(
                    Texts.ERR_NO_REGION_FOUND,
                    regionId
            )));
            return false;
        }
        return true;
    }

    public static boolean canModifyRegion(CommandSource source, Region region, String regionId) {
        if(source.hasPermission(Permissions.CAN_MODIFY_NON_OWNING_REGIONS))
            return true;

        if(!RegionUtils.canModifyByCommand(region, source)) {
            MessageUtils.sendError(source, Text.of(TextManager.getText(
                    Texts.ERR_NOT_REGION_OWNER,
                    regionId
            )));
            return false;
        }
        return true;
    }
    public static boolean canRemoveRegion(CommandSource source, Region region, String regionId) {
        if(source.hasPermission(Permissions.CAN_REMOVE_NON_OWNING_REGIONS))
            return true;

        if(!RegionUtils.canModifyByCommand(region, source)) {
            MessageUtils.sendError(source, Text.of(TextManager.getText(
                    Texts.ERR_NOT_REGION_OWNER,
                    regionId
            )));
            return false;
        }
        return true;
    }

    public static boolean isPlayerExecuteCmd(CommandSource source) {
        if (!(source instanceof Player)) {
            MessageUtils.sendError(source, Text.of(TextManager.getText(Texts.ERR_CMD_ONLY_FOR_PLAYERS)));
            return false;
        }
        return true;
    }
}
