package me.doggy.justguard;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import javafx.util.Pair;
import me.doggy.justguard.config.ConfigManager;
import me.doggy.justguard.flag.Flags;
import me.doggy.justguard.region.Region;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.spongepowered.api.world.World;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RegionsHolder {

    private static final Logger logger = JustGuard.getInstance().getLogger();
    private static final ConfigManager configManager = JustGuard.getInstance().getConfigManager();

    private static final Map<String, Region> REGIONS = new HashMap<>();
    private static final List<Region> REGIONS_TO_REMOVE = new ArrayList<>();

    public static boolean addRegion(String name, Region region) {
        if(REGIONS.containsKey(name) || REGIONS.containsValue(region))
            return false;

        REGIONS.put(name, region);
        return true;
    }
    public static boolean removeRegion(String regionName) {
        Region region = REGIONS.remove(regionName);
        if(region == null)
            return false;
        REGIONS_TO_REMOVE.add(region);
        return true;
    }
    public static void removeRegionsByWorld(World world) {
        Iterator<Map.Entry<String, Region>> i = REGIONS.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, Region> regionEntry = i.next();
            if(regionEntry.getValue().getWorld().equals(world)) {
                removeRegion(regionEntry.getKey());
            }
        }
    }
    public static boolean hasRegion(String regionName) {
        return REGIONS.containsKey(regionName);
    }
    public static boolean hasRegion(Region region) {
        return REGIONS.containsValue(region);
    }
    public static String getRegionName(Region region) {
        Optional<Map.Entry<String, Region>> entryOpt = REGIONS.entrySet().stream().filter(x->x.getValue().equals(region)).findFirst();
        if(entryOpt.isPresent())
            return entryOpt.get().getKey();
        return null;
    }

    @Nullable
    public static Region getRegion(String name) {
        return REGIONS.get(name);
    }

    public static boolean changeRegionName(String oldName, String newName) {
        Region region = REGIONS.get(oldName);
        if(region == null)
            return false;
        if(REGIONS.containsKey(newName))
            return false;

        REGIONS.remove(oldName);
        REGIONS.put(newName, region);
        return true;
    }

    public static Map<String, Region> getRegions() {
        return REGIONS.entrySet().stream().collect(Collectors.toMap(x->x.getKey(), x->x.getValue()));
    }
    public static Map<String, Region> getRegions(Predicate<Map.Entry<String, Region>> predicate) {
        return REGIONS.entrySet().stream()
                .filter(predicate)
                .collect(Collectors.toMap(x->x.getKey(), x->x.getValue()));
    }

    private static final String MAIN_FILE_NAME = "region";
    private static final String FLAGS_FILE_NAME = "flags";

    public static final Type regionPairType = new TypeToken<Pair<String, Region>>(){}.getType();
    private static boolean loadRegion(File directory) {
        File mainFile = new File(directory, MAIN_FILE_NAME);
        File flagsFile = new File(directory, FLAGS_FILE_NAME);

        BufferedReader bufferedReader = null;

        boolean loaded = true;
        try {
            bufferedReader = new BufferedReader(new FileReader(mainFile));

            Pair<String, Region> regionPair = new Gson().fromJson(bufferedReader, regionPairType);

            ConfigurationLoader<CommentedConfigurationNode> loader =
                    HoconConfigurationLoader.builder().setPath(flagsFile.toPath()).build();

            ConfigurationNode flags = loader.load();
            regionPair.getValue().setFlags(new Flags(flags));

            REGIONS.put(regionPair.getKey(), regionPair.getValue());
        } catch (IOException e) {
            loaded = false;
            logger.error("Error when loading region '"+directory.toString()+"'. Exception: "+ e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    loaded = false;
                    logger.error("Error when loading region '"+directory.toString()+"'. Exception: "+ e);
                }
            }
        }
        return loaded;
    }
    public static void loadRegionsByWorldName(String worldName) {
        logger.debug("Started loading regions in world '"+worldName+"'");
        File worldDir = configManager.getRegionsDirByWorld(worldName);
        if(worldDir.exists()) {
            for (final File fileEntry : worldDir.listFiles()) {
                if (fileEntry.isDirectory()) {
                    loadRegion(fileEntry);
                }
            }
        }
        logger.debug("Finished loading regions in world '"+worldName+"'");
    }
    public static void loadRegionsByWorld(World world) {
        loadRegionsByWorldName(world.getName());
    }
    public static void loadRegions() {
        logger.debug("Started loading regions");
        File regionsDir = configManager.getRegionsDir();
        for (final File fileEntry : regionsDir.listFiles()) {
            if (fileEntry.isDirectory()) {
                String worldName = fileEntry.getName();
                loadRegionsByWorldName(worldName);
            }
        }
        logger.debug("Finished loading regions");
    }


    public static void removeRegionFromFiles(Region region) {
        File currDir = configManager.getRegionDir(region);
        File mainFile = new File(currDir, MAIN_FILE_NAME);
        File flagsFile = new File(currDir, FLAGS_FILE_NAME);
        mainFile.delete();
        flagsFile.delete();
        currDir.delete();
    }
    private static boolean save(String regionName, Region region) {
        File currDir = configManager.getRegionDir(region);
        currDir.mkdirs();
        File mainFile = new File(currDir, MAIN_FILE_NAME);
        File flagsFile = new File(currDir, FLAGS_FILE_NAME);

        FileWriter fileWriter = null;
        boolean saved = true;
        try {
            if (!mainFile.exists())
                mainFile.createNewFile();
            fileWriter = new FileWriter(mainFile);

            Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
            fileWriter.write(gson.toJson(new Pair<String, Region>(regionName, region), regionPairType));

            ConfigurationLoader<CommentedConfigurationNode> loader =
                    HoconConfigurationLoader.builder().setPath(flagsFile.toPath()).build();

            region.getFlags().save(loader);
        } catch (IOException e) {
            saved = false;
            logger.error("Error when saving region '"+region.getUniqueId().toString()+"'. Exception: "+ e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    saved = false;
                    logger.error("Error when saving region '"+region.getUniqueId().toString()+"'. Exception: "+ e);
                }
            }
        }
        return saved;
    }
    public static void saveRegionsByWorld(World world){
        for (Map.Entry<String, Region> regionEntry : REGIONS.entrySet()) {
            if(regionEntry.getValue().getWorld().equals(world))
                save(regionEntry.getKey(), regionEntry.getValue());
        }

        List<Region> regionsToRemoveByWorld = new ArrayList<>();
        REGIONS_TO_REMOVE.forEach(r ->{
            if(r.getWorld().equals(world))
                regionsToRemoveByWorld.add(r);
        });
        REGIONS_TO_REMOVE.removeAll(regionsToRemoveByWorld);
        regionsToRemoveByWorld.forEach(r -> removeRegionFromFiles(r));
    }
    public static void saveRegions(){
        for (Map.Entry<String, Region> regionEntry : REGIONS.entrySet()) {
            save(regionEntry.getKey(), regionEntry.getValue());
        }
        REGIONS_TO_REMOVE.forEach(r -> removeRegionFromFiles(r));
        REGIONS_TO_REMOVE.clear();
    }
}
