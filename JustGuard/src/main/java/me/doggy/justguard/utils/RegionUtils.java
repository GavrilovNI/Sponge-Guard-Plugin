package me.doggy.justguard.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.ConfigManager;
import me.doggy.justguard.consts.Permissions;
import me.doggy.justguard.flag.Flags;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.help.MyAABB;
import me.doggy.justguard.utils.help.RegionPair;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class RegionUtils {

    private static ConfigManager configManager = JustGuard.getInstance().getConfigManager();
    private static Logger logger = JustGuard.getInstance().getLogger();

    public static final String MAIN_FILE_NAME = "region";
    public static final String FLAGS_FILE_NAME = "flags";

    public static File getRegionsDirByWorld(String worldName) {
        File regionsDir = configManager.getRegionsDir();
        return new File(regionsDir, worldName);
    }
    public static File getRegionDir(Region region) {
        File worldDir = getRegionsDirByWorld(region.getWorld().getName());
        return new File(worldDir, region.getUUID().toString());
    }


    public static boolean save(RegionPair regionPair) {
        File currDir = getRegionDir(regionPair.region);
        currDir.mkdirs();
        File mainFile = new File(currDir, MAIN_FILE_NAME);
        File flagsFile = new File(currDir, FLAGS_FILE_NAME);

        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        FileWriter fileWriter = null;
        boolean saved = true;
        try {
            if (!mainFile.exists())
                mainFile.createNewFile();
            fileWriter= new FileWriter(mainFile);
            fileWriter.write(gson.toJson(regionPair));

            ConfigurationLoader<CommentedConfigurationNode> loader =
                    HoconConfigurationLoader.builder().setPath(flagsFile.toPath()).build();

            regionPair.region.getFlags().save(loader);

        } catch (IOException e) {
            saved = false;
            logger.error("Error when saving region '"+regionPair.region.getUUID().toString()+"'. Exception: "+ e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    saved = false;
                    logger.error("Error when saving region '"+regionPair.region.getUUID().toString()+"'. Exception: "+ e);
                }
            }
        }
        return saved;
    }
    public static boolean save(String name, Region region) {
        return save(new RegionPair(name, region));
    }
    public static RegionPair load(File directory) {
        File mainFile = new File(directory, MAIN_FILE_NAME);
        File flagsFile = new File(directory, FLAGS_FILE_NAME);

        RegionPair result = null;

        Gson gson = new Gson();
        BufferedReader bufferedReader = null;
        try {

            Type gsonType = new TypeToken<RegionPair>() {}.getType();

            bufferedReader = new BufferedReader(new FileReader(mainFile));
            result = gson.fromJson(bufferedReader, gsonType);

            ConfigurationLoader<CommentedConfigurationNode> loader =
                    HoconConfigurationLoader.builder().setPath(flagsFile.toPath()).build();

            ConfigurationNode flags = loader.load();
            result.region.setFlags(new Flags(flags));

        } catch (IOException e) {
            logger.error("Error when loading region '"+directory.toString()+"'. Exception: "+ e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    logger.error("Error when loading region '"+directory.toString()+"'. Exception: "+ e);
                }
            }
        }

        return result;
    }
    public static void removeRegionFromFiles(Region region) {
        File currDir = getRegionDir(region);
        File mainFile = new File(currDir, MAIN_FILE_NAME);
        File flagsFile = new File(currDir, FLAGS_FILE_NAME);
        mainFile.delete();
        flagsFile.delete();
        currDir.delete();
    }

    public static HashMap<String, Region> getAllRegions() {
        return JustGuard.REGIONS;
    }
    public static List<RegionPair> swapToList(Map<String, Region> regions) {
        List<RegionPair> result = new ArrayList<>();
        for(Map.Entry<String, Region> entry : regions.entrySet())
        {
            result.add(new RegionPair(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    public static List<RegionPair> getRegionsInLocation(Location<World> location) {
        List<RegionPair> regions = new ArrayList<>();

        for(Map.Entry<String, Region> regionEntry : getAllRegions().entrySet()) {
            String name = regionEntry.getKey();
            Region region = regionEntry.getValue();

            if(region.contains(location))
            {
                regions.add(new RegionPair(name, region));
            }
        }

        return regions;
    }

    public static List<RegionPair> getHighestPriorityRegions(List<RegionPair> regions) {
        List<RegionPair> result = new ArrayList<>();

        int priority = 0;
        for (RegionPair regionPair : regions)
        {
            int currWeight = regionPair.region.getPriority();
            if(currWeight > priority)
            {
                result.clear();
                priority = currWeight;
                result.add(regionPair);
            }
            else if (currWeight == priority)
            {
                result.add(regionPair);
            }
        }

        return result;
    }
    public static List<RegionPair> getRegionsByOwnership(List<RegionPair> regions, Player player, Region.PlayerOwnership ownership) {
        List<RegionPair> result = new ArrayList<>();

        for (RegionPair regionPair : regions)
        {
            if(regionPair.region.getPlayerOwnership(player).equals(ownership))
                result.add(regionPair);
        }

        return result;
    }

    //given region won't be in return list
    public static List<RegionPair> getRegionsIntersectWith(World world, MyAABB bounds) {
        List<RegionPair> regions = new ArrayList<>();

        for(Map.Entry<String, Region> regionEntry : getAllRegions().entrySet()) {
            String name = regionEntry.getKey();
            Region currRegion = regionEntry.getValue();

            if(currRegion.intersects(world, bounds))
            {
                regions.add(new RegionPair(name, currRegion));
            }
        }

        return regions;
    }
    public static List<RegionPair> getRegionsIntersectWith(Region region) {
        return getRegionsIntersectWith(region.getWorld(), region.getBounds());
    }

    public static boolean canModify(Region region, CommandSource source)
    {
        if(source.hasPermission(Permissions.CAN_MODIFY_NON_OWNING_REGIONS))
            return true;

        if(source instanceof Player)
            return region.isOwner((Player) source);

        return false;
    }

}
