package net.fabricmc.safetweaks.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.safetweaks.SafeTweaksClient;

public class FeatureFlagManager {

    public interface setDefaultCallback {
        public void run(FeatureFlagManager i);
    }

    public class FeatureMissingException extends RuntimeException {
        public FeatureMissingException(String msg) {
            super(msg);
        }
    }

    private static final FeatureFlagManager instance = new FeatureFlagManager();
    private final String filePath = FabricLoader.getInstance().getConfigDir().resolve("safetweaks-feature-flags.txt").toString();

    private HashMap<String, Boolean> FeatureFlags = new HashMap<String, Boolean>();

    private FeatureFlagManager() {
        setFromFile(filePath);
    }

    public static FeatureFlagManager getInstance() {
        return instance;
    }

    public static FeatureFlagManager getInstance(setDefaultCallback overrideFlags) {
        overrideFlags.run(instance);
        return instance;
    }

    public Boolean isEmpty() {
        return FeatureFlags.isEmpty();
    }

    public void set(String key, Boolean flag) {
        FeatureFlags.put(key, flag);
    }

    public Boolean get(String key) throws FeatureMissingException {
        if(FeatureFlags.keySet().contains(key)) {
            return FeatureFlags.get(key);
        } else {
            throw new FeatureMissingException("Key not found in features: " + key);
        }
    }

    // Set FeatureFlags from file for persistent flags
    private void setFromFile(String filePath) {
        try {
            File file = new File(filePath);
            file.createNewFile(); // Does nothing if file exists already
        } catch (IOException e) {
            SafeTweaksClient.LOGGER.error("Error reading flags: " + e.getMessage());
        }


        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String[] keyVal;
            for(String line = br.readLine(); line != null; line = br.readLine()) {
                keyVal = line.split("=");
                FeatureFlags.put(keyVal[0], Boolean.parseBoolean(keyVal[1]));
            }
        } catch (IOException e) {
            SafeTweaksClient.LOGGER.error("Could not read flags from file: " + e.getMessage());
        }
    }

    // Write FeatureFlags to file in order to read on next init
    private static void writeToFile(String path, HashMap<String, Boolean> flags) {
        try {
            File file = new File(path);
            file.createNewFile(); // Does nothing if file exists already

            PrintWriter pr = new PrintWriter(new File(path));
            for (Map.Entry<String, Boolean> entry : flags.entrySet()) {
                pr.println(entry.getKey() + "=" + entry.getValue().toString());
            }
            pr.close();
        } catch (FileNotFoundException e) {
            SafeTweaksClient.LOGGER.error("Error writing flags: " + e.getMessage());
        } catch (IOException e) {
            SafeTweaksClient.LOGGER.error("Error writing flags: " + e.getMessage());
        }
    }

    // Wrapper for writing feature flags to file
    public static void saveFlagsPersistent() {
        writeToFile(instance.filePath, instance.FeatureFlags);
    }
}
