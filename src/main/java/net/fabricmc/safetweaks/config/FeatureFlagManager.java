package net.fabricmc.safetweaks.config;

import java.util.HashMap;

public class FeatureFlagManager {

    public class FeatureMissingException extends RuntimeException {
        public FeatureMissingException(String msg) {
            super(msg);
        }
    }

    private static final FeatureFlagManager instance = new FeatureFlagManager();

    private HashMap<String, Boolean> FeatureFlags = new HashMap<String, Boolean>();

    private FeatureFlagManager() {}

    public static FeatureFlagManager getInstance() {
        return instance;
    }

    public void set(String key, Boolean flag) {
        FeatureFlags.put(key, flag);
    }

    public Boolean get(String key) throws FeatureMissingException {
        if(FeatureFlags.keySet().contains(key)) {
            return FeatureFlags.get(key);
        } else {
            throw new FeatureMissingException("Key does not found in features: " + key);
        }
    }
}
