package me.doggy.justguard.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.util.Pair;
import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.ConfigManager;
import me.doggy.justguard.region.GlobalRegion;
import me.doggy.justguard.region.LocalRegion;
import me.doggy.justguard.region.Region;
import me.doggy.justguard.utils.help.RegionPair;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegionUtils {

    private static ConfigManager configManager = JustGuard.getInstance().getConfigManager();
    private static Logger logger = JustGuard.getInstance().getLogger();

    public static final String MAIN_FILE_NAME = "region";
    public static final String FLAGS_FILE_NAME = "flags";


    private class RegionNull extends Region
    {
        public RegionNull(RegionType regionType, ConfigurationNode flags) {
            super(regionType, flags);
        }

        @Override
        public World getWorld() {
            return null;
        }

        @Override
        public <E extends World> boolean isInside(Location<E> location) {
            return false;
        }
    }


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

    public static RegionPair<Region> load(File directory)
    {
        File mainFile = new File(directory, MAIN_FILE_NAME);
        File flagsFile = new File(directory, FLAGS_FILE_NAME);

        RegionPair<Region> result = null;

        Gson gson = new Gson();
        BufferedReader bufferedReader = null;
        try {

            Type helpType = new TypeToken<RegionPair<RegionNull>>() {}.getType();

            bufferedReader = new BufferedReader(new FileReader(mainFile));
            RegionPair<RegionNull> regionTypeHelpClassPair = gson.fromJson(bufferedReader, helpType);

            bufferedReader.close();
            bufferedReader = new BufferedReader(new FileReader(mainFile));

            Type regionGsonType = null;
            switch (regionTypeHelpClassPair.region.getRegionType())
            {
                case Local:
                    regionGsonType = new TypeToken<RegionPair<LocalRegion>>() {}.getType();
                    break;
                case Global:
                    regionGsonType = new TypeToken<RegionPair<GlobalRegion>>() {}.getType();
                    break;
                default:
                    return null;
            }


            result = gson.fromJson(bufferedReader, regionGsonType);

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




}
