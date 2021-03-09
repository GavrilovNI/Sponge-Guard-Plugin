package me.doggy.justguard.permission;

import me.doggy.justguard.JustGuard;

public class Permissions {

    private static final String PREFIX = JustGuard.PLUGIN_ID + ".";

    private static final String COMMAND_PREFIX = PREFIX + "command.";

    public static final String COMMAND_BASE = COMMAND_PREFIX + "base";
    public static final String COMMAND_VERSION = COMMAND_PREFIX + "version";
    public static final String COMMAND_RELOAD = COMMAND_PREFIX + "reload";
    public static final String COMMAND_WAND = COMMAND_PREFIX + "wand";


    private static final String COMMAND_REGION_PREFIX = COMMAND_PREFIX + "region.";

    public static final String COMMAND_REGION_BASE = COMMAND_REGION_PREFIX + "base";
    public static final String COMMAND_REGION_CREATE = COMMAND_REGION_PREFIX + "create";
    public static final String COMMAND_REGION_SETBOUND = COMMAND_REGION_PREFIX + "setbound";
    public static final String COMMAND_REGION_CLAIM = COMMAND_REGION_PREFIX + "claim";
    public static final String COMMAND_REGION_LIST = COMMAND_REGION_PREFIX + "list";
    public static final String COMMAND_REGION_INFO = COMMAND_REGION_PREFIX + "info";
    public static final String COMMAND_REGION_SETPLAYERSTATE = COMMAND_REGION_PREFIX + "setplayerstate";


    private static final String REGION_CLAIM_PREFIX = PREFIX + "region.claim.";
    //public static final String CAN_CLAIM_REGION_OF_TYPE_PREFIX = REGION_CLAIM_PREFIX + "type.";

    private static final String REGION_CLAIM_INTERSECTWITH_PREFIX = REGION_CLAIM_PREFIX + "intersectwith.";
    //public static final String CAN_INTERSECT_REGION_OF_TYPE_PREFIX = REGION_CLAIM_INTERSECTWITH_PREFIX + "type.";
    public static final String CAN_INTERSECT_REGION_WITH_OWNERSHIP_PREFIX = REGION_CLAIM_INTERSECTWITH_PREFIX + "ownership.";
}
