package me.doggy.justguard.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.doggy.justguard.JustGuard;
import me.doggy.justguard.config.ConfigManager;
import me.doggy.justguard.region.GlobalRegion;
import me.doggy.justguard.region.LocalRegion;
import me.doggy.justguard.region.Region;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.world.World;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.UUID;

public class RegionUtils {

    private static ConfigManager configManager = JustGuard.getInstance().getConfigManager();
    private static Logger logger = JustGuard.getInstance().getLogger();

    public static final String MAIN_FILE_NAME = "region";
    public static final String FLAGS_FILE_NAME = "flags";


    private class RegionTypeHelpClass
    {
        public Region.RegionType regionType;
    }

    public static File getRegionDir(Region region)
    {
        File regionsDir = configManager.getRegionsDir();
        File worldDir = new File(regionsDir, region.getWorld().getName());
        return new File(worldDir, region.getUUID().toString());
    }


    public static boolean save(Region region)
    {
        File currDir = getRegionDir(region);
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
            fileWriter.write(gson.toJson(region));

            ConfigurationLoader<CommentedConfigurationNode> loader =
                    HoconConfigurationLoader.builder().setPath(flagsFile.toPath()).build();

            loader.save(region.getFlags());

        } catch (IOException e) {
            saved = false;
            logger.error("Error when saving region '"+region.getUUID().toString()+"'. Exception: "+ e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    saved = false;
                    logger.error("Error when saving region '"+region.getUUID().toString()+"'. Exception: "+ e);
                }
            }
        }
        return saved;

    }

    public static Region load(File directory)
    {
        File mainFile = new File(directory, MAIN_FILE_NAME);
        File flagsFile = new File(directory, FLAGS_FILE_NAME);

        Region result = null;

        Gson gson = new Gson();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(mainFile));
            RegionTypeHelpClass regionTypeHelpClass = gson.fromJson(bufferedReader, RegionTypeHelpClass.class);

            bufferedReader.close();
            bufferedReader = new BufferedReader(new FileReader(mainFile));

            switch (regionTypeHelpClass.regionType)
            {
                case Local:
                    result = gson.fromJson(bufferedReader, LocalRegion.class);
                    break;
                case Global:
                    result = gson.fromJson(bufferedReader, GlobalRegion.class);
                    break;
            }

            ConfigurationLoader<CommentedConfigurationNode> loader =
                    HoconConfigurationLoader.builder().setPath(flagsFile.toPath()).build();

            ConfigurationNode flags = loader.load();

            result.setFlags(flags);

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
