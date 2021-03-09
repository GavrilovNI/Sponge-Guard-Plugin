package me.doggy.justguard.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.util.Pair;
import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.ConfigManager;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.help.RegionPair;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.*;

public class RegionUtils {

    private static ConfigManager configManager = JustGuard.getInstance().getConfigManager();
    private static Logger logger = JustGuard.getInstance().getLogger();

    public static final String MAIN_FILE_NAME = "region";
    public static final String FLAGS_FILE_NAME = "flags";

    public static File getRegionsDirByWorld(String worldName)
    {
        File regionsDir = configManager.getRegionsDir();
        return new File(regionsDir, worldName);
    }
    public static File getRegionDir(Region region)
    {
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

            loader.save(regionPair.region.getFlags());

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
            result.region.setFlags(flags);

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

    public static void removeRegionFromFiles(Region region)
    {
        File currDir = getRegionDir(region);
        File mainFile = new File(currDir, MAIN_FILE_NAME);
        File flagsFile = new File(currDir, FLAGS_FILE_NAME);
        mainFile.delete();
        flagsFile.delete();
        currDir.delete();
    }

    public static HashMap<String, Region> getAllRegions()
    {
        return JustGuard.REGIONS;
    }

    public static List<RegionPair> getRegionsInLocation(Location<World> location)
    {
        List<RegionPair> regions = new ArrayList<>();

        for(Map.Entry<String, Region> regionEntry : getAllRegions().entrySet()) {
            String name = regionEntry.getKey();
            Region region = regionEntry.getValue();

            /*logger.info("here1: "+name);
            logger.info("here2: "+((LocalRegion)region).getBounds().getMin().toString());
            logger.info("here3: "+((LocalRegion)region).getBounds().getMax().toString());
            logger.info("here4: "+region.getWorld().getName().toString());
            logger.info("here5: "+location.getPosition().toString());
            logger.info("here6: "+location.getExtent().getName().toString());
            logger.info("here7: "+String.valueOf(region.isInside(location)));*/

            if(region.contains(location))
            {
                regions.add(new RegionPair(name, region));
            }
        }

        return regions;
    }
    public static List<RegionPair> getMoreWeightableRegions(List<RegionPair> regions)
    {
        List<RegionPair> result = new ArrayList<>();

        int weight = 0;
        for (RegionPair regionPair : regions)
        {
            int currWeight = regionPair.region.getWeight();
            if(currWeight > weight)
            {
                result.clear();
                weight = currWeight;
                result.add(regionPair);
            }
            else if (currWeight == weight)
            {
                result.add(regionPair);
            }
        }

        return result;
    }
    //given region won't be in return list
    public static List<RegionPair> getRegionsIntersectWith(Region region)
    {
        return getRegionsIntersectWith(region.getWorld(), region.getBounds());
    }

    public static List<RegionPair> getRegionsIntersectWith(World world, AABB bounds)
    {
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


}
