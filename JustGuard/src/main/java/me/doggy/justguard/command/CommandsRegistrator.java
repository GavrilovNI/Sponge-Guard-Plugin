package me.doggy.justguard.command;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.Pending;
import me.doggy.justguard.command.region.*;
import me.doggy.justguard.permission.Permissions;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.help.AABBBuilder;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandsRegistrator {

    public static final String REGION_ID = "region-id";
    public static final String PAGE = "page";

    public static void register()
    {
        CommandSpec cmdRegionCreate = CommandSpec.builder()
                .description(Text.of("No command description"))
                .permission(Permissions.COMMAND_REGION_CREATE)
                .arguments(GenericArguments.world(Text.of("world")),
                            GenericArguments.enumValue(Text.of("region-type"), Pending.RegionType.class))
                .executor(new CommandCreate())
                .build();

        CommandSpec cmdRegionSetBound = CommandSpec.builder()
                .description(Text.of("No command description"))
                .permission(Permissions.COMMAND_REGION_SETBOUND)
                .arguments(GenericArguments.enumValue(Text.of("type"), AABBBuilder.BoundType.class),
                        GenericArguments.integer(Text.of("pos-x")),
                        GenericArguments.integer(Text.of("pos-y")),
                        GenericArguments.integer(Text.of("pos-z")))
                .executor(new CommandSetBound())
                .build();

        CommandSpec cmdRegionClaim = CommandSpec.builder()
                .description(Text.of("No command description"))
                .permission(Permissions.COMMAND_REGION_CLAIM)
                .arguments(GenericArguments.string(Text.of(REGION_ID)))
                .executor(new CommandClaim())
                .build();

        CommandSpec cmdRegionList = CommandSpec.builder()
                .description(Text.of("No command description"))
                .permission(Permissions.COMMAND_REGION_LIST)
                .arguments(GenericArguments.optional(GenericArguments.integer(Text.of(PAGE))))
                .executor(new CommandList())
                .build();

        CommandSpec cmdRegionInfo = CommandSpec.builder()
                .description(Text.of("No command description"))
                .permission(Permissions.COMMAND_REGION_INFO)
                .arguments(GenericArguments.optional(GenericArguments.integer(Text.of(PAGE))))
                .executor(new CommandInfo())
                .build();

        CommandSpec cmdRegionSetOwnership = CommandSpec.builder()
                .description(Text.of("No command description"))
                .permission(Permissions.COMMAND_REGION_SETOWNERSHIP)
                .arguments(GenericArguments.string(Text.of(REGION_ID)),
                        GenericArguments.player(Text.of("player")),
                        GenericArguments.enumValue(Text.of("state"), Region.PlayerState.class))
                .executor(new CommandSetOwnership())
                .build();

        CommandSpec cmdRegionRemove = CommandSpec.builder()
                .description(Text.of("No command description"))
                .permission(Permissions.COMMAND_REGION_REMOVE)
                .arguments(GenericArguments.string(Text.of(REGION_ID)))
                .executor(new CommandRemove())
                .build();

        CommandSpec cmdRegion = CommandSpec.builder()
                .description(Text.of("No command description"))
                .permission(Permissions.COMMAND_REGION_BASE)
                .child(cmdRegionCreate, "create")
                .child(cmdRegionSetBound, "setbound")
                .child(cmdRegionClaim, "claim")
                .child(cmdRegionList, "list")
                .child(cmdRegionInfo, "info")
                .child(cmdRegionSetOwnership, "setownership")
                .child(cmdRegionRemove, "remove", "rem", "rm")
                .build();




        CommandSpec cmdVersion = CommandSpec.builder()
                .description(Text.of("No command description"))
                .permission(Permissions.COMMAND_VERSION)
                .executor(new CommandVersion())
                .build();

        CommandSpec cmdReload = CommandSpec.builder()
                .description(Text.of("No command description"))
                .permission(Permissions.COMMAND_RELOAD)
                .executor(new CommandReload())
                .build();

        CommandSpec cmdSave = CommandSpec.builder()
                .description(Text.of("No command description"))
                .permission(Permissions.COMMAND_SAVE)
                .executor(new CommandSave())
                .build();

        CommandSpec cmdWand = CommandSpec.builder()
                .description(Text.of("No command description"))
                .permission(Permissions.COMMAND_WAND)
                .executor(new CommandWand())
                .build();

        CommandSpec cmdJustGuard = CommandSpec.builder()
                .description(Text.of("No command description"))
                .permission(Permissions.COMMAND_BASE)
                .executor(new CommandJustGuard())
                .child(cmdVersion, "version")
                .child(cmdReload, "reload")
                .child(cmdSave, "save")
                .child(cmdRegion, "region", "rg")
                .child(cmdWand, "wand")
                .build();

        JustGuard plugin = JustGuard.getInstance();
        Sponge.getCommandManager().register(plugin, cmdJustGuard, "justguard", "jg");
        plugin.getLogger().debug("Commands loaded successfully!");
    }

}
