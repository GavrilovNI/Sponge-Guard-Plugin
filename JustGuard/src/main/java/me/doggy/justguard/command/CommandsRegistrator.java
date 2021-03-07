package me.doggy.justguard.command;

import me.doggy.justguard.JustGuard;
import me.doggy.justguard.command.region.CommandClaim;
import me.doggy.justguard.command.region.CommandCreate;
import me.doggy.justguard.command.region.CommandList;
import me.doggy.justguard.command.region.CommandSetBound;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.Bounds;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandsRegistrator {

    public static void register()
    {
        CommandSpec cmdRegionCreate = CommandSpec.builder()
                .description(Text.of("No command description"))
                .permission("safeguard.command.region.create")
                .arguments(GenericArguments.world(Text.of("world")),
                            GenericArguments.enumValue(Text.of("region-type"), Region.RegionType.class))
                .executor(new CommandCreate())
                .build();

        CommandSpec cmdRegionSetBound = CommandSpec.builder()
                .description(Text.of("No command description"))
                .permission("safeguard.command.region.setbound")
                .arguments(GenericArguments.enumValue(Text.of("type"), Bounds.BoundType.class),
                        GenericArguments.integer(Text.of("pos-x")),
                        GenericArguments.integer(Text.of("pos-y")),
                        GenericArguments.integer(Text.of("pos-z")))
                .executor(new CommandSetBound())
                .build();

        CommandSpec cmdRegionClaim = CommandSpec.builder()
                .description(Text.of("No command description"))
                .permission("safeguard.command.region.claim")
                .arguments(GenericArguments.string(Text.of("name")))
                .executor(new CommandClaim())
                .build();

        CommandSpec cmdRegionList = CommandSpec.builder()
                .description(Text.of("No command description"))
                .permission("safeguard.command.region.list")
                .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("page"))))
                .executor(new CommandList())
                .build();

        CommandSpec cmdRegion = CommandSpec.builder()
                .description(Text.of("No command description"))
                .permission("safeguard.command.region")
                .child(cmdRegionCreate, "create")
                .child(cmdRegionSetBound, "setbound")
                .child(cmdRegionClaim, "claim")
                .child(cmdRegionList, "list")
                //.child(cmdVersion, "addowner")
                //.child(cmdVersion, "addmember")
                //.child(cmdVersion, "removeowner")
                //.child(cmdVersion, "removemember")
                .build();






        CommandSpec cmdVersion = CommandSpec.builder()
                .description(Text.of("No command description"))
                .permission("safeguard.command.version")
                .executor(new CommandVersion())
                .build();

        CommandSpec cmdReload = CommandSpec.builder()
                .description(Text.of("No command description"))
                .permission("safeguard.command.reload")
                .executor(new CommandReload())
                .build();

        CommandSpec cmdWand = CommandSpec.builder()
                .description(Text.of("No command description"))
                .permission("safeguard.command.wand")
                .executor(new CommandWand())
                .build();

        CommandSpec cmdJustGuard = CommandSpec.builder()
                .description(Text.of("No command description"))
                .permission("safeguard.command.justguard")
                .executor(new CommandJustGuard())
                .child(cmdVersion, "version")
                .child(cmdReload, "reload")
                .child(cmdRegion, "region", "rg")
                .child(cmdWand, "wand")
                .build();

        JustGuard plugin = JustGuard.getInstance();
        Sponge.getCommandManager().register(plugin, cmdJustGuard, "justguard", "jg");
        plugin.getLogger().debug("Commands loaded successfully!");
    }

}
