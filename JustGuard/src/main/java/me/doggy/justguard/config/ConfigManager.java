package me.doggy.justguard.config;

import com.flowpowered.math.vector.Vector3d;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.doggy.justguard.JustGuard;
import me.doggy.justguard.region.LocalRegion;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.Bounds;
import me.doggy.justguard.utils.FileUtils;
import me.doggy.justguard.utils.RegionUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class ConfigManager {

    private static Logger logger = JustGuard.getInstance().getLogger();

    private File configDir;
    private File regionsDir;

    public ConfigManager(Path configDir)
    {
        this.configDir = configDir.toFile();
        this.configDir.mkdirs();

        this.regionsDir = new File(this.configDir, "regions");
        this.regionsDir.mkdirs();
    }

    private Map<String, Boolean> flags;
    //private ConfigurationNode serverProperties;

    public File getConfigDir() { return configDir; }
    public File getRegionsDir() { return regionsDir; }
    public boolean getFlag(String flag)
    {
        return flags.getOrDefault(flag.toLowerCase(), true);
    }
    //public ConfigurationNode getServerProperty(String name) { return serverProperties.getNode("name"); }

    public void loadConfig() {

        loadFlags();
        //loadServerProperties();

        logger.info("Config loaded.");
    }

    /*public void loadServerProperties()
    {
        String name = "server.properties";
        logger.debug("Loading '"+name+"'.");
        ConfigurationNode serverProperties = getFileNode(name, FileUtils.getServerDir());

    }*/

    public void loadFlags()
    {
        ConfigurationNode rootNode = FileUtils.getFileNode("flags.conf", configDir);

        flags = new HashMap<>();

        for (Map.Entry<Object, ? extends ConfigurationNode> entry : rootNode.getChildrenMap().entrySet())
        {
            String keyStr = entry.getKey().toString();
            Boolean value = entry.getValue().getBoolean(true);
            flags.put(keyStr.toLowerCase(), value);
        }
    }





    Region reg;
    Region regNew;

    public void test()
    {
        ConfigurationNode rootNode = FileUtils.getFileNode("defaultFlags.conf", configDir);

        if(reg == null)
        {
            Bounds bounds = new Bounds(Sponge.getServer().getWorld("world").get(), new Vector3d(1,2,3), new Vector3d(4,5,6));
            reg = new LocalRegion(rootNode, bounds);
            RegionUtils.save(reg);
            File currDir = RegionUtils.getRegionDir(reg);
            regNew = RegionUtils.load(currDir);
        }





        RegionUtils.save(regNew);

        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        logger.info(gson.toJson(regNew));

        ConfigurationNode x = regNew.getFlag("player", "member", "block.break3", "minecraft:redstone");
        logger.info("123: "+String.valueOf(x.isVirtual()));
        logger.info("124: "+String.valueOf(x.getBoolean()));

        //Region reg = new Region<World>(rootNode);

        /*String value = String.valueOf(reg.getValue("player", "owner", "block.break","minecraft:stone").getBoolean());
        logger.info(value);
        value = String.valueOf(reg.getValue("player", "owner", "block.place","minecraft:stone").getBoolean());
        logger.info(value);
        value = String.valueOf(reg.getValue("player", "owner", "entity.break","minecraft:painting").getBoolean());
        logger.info(value);
        value = String.valueOf(reg.getValue("player", "member", "block.place","minecraft:wood").getBoolean());
        logger.info(value);
        value = String.valueOf(reg.getValue("player", "member", "block.place","minecraft:stone").getBoolean());
        logger.info(value);

        value = String.valueOf(reg.getValue("player", "member", "entity.err1","minecraft:stone").isVirtual());
        logger.info(value);
        value = String.valueOf(reg.getValue("player", "owner", "entity.err1","minecraft:stone").isVirtual());
        logger.info(value);*/
    }
}
