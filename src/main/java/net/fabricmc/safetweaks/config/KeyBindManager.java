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
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class KeyBindManager {

    public interface callback {
        public void run(KeyBindManager instance);
    }

    private static final KeyBindManager instance = new KeyBindManager();
    private final String filePath = FabricLoader.getInstance().getConfigDir().resolve("safetweaks-key-bindings.txt").toString();
    private final String category = "config.safetweaks.title";

    private HashMap<String, KeyBinding> KeyBinds = new HashMap<String, KeyBinding>();

    private KeyBindManager() {
        setFromFile(filePath);
    }

    public static KeyBindManager getInstance(callback cb) {
        cb.run(instance);
        return instance;
    }

    public static KeyBindManager getInstance() {
        return instance;
    }

    public HashMap<String, KeyBinding> getKeyBindings() {
        return KeyBinds;
    }

    public KeyBinding get(String id) {
        if(KeyBinds.keySet().contains(id)) {
            return KeyBinds.get(id);
        }
        KeyBinding placeholderBinding = new KeyBinding(id, InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), category);
        set(id, placeholderBinding);
        return placeholderBinding;
    }

    public void set(String id, KeyBinding keyBind) {
        KeyBinds.put(id, keyBind);
    }

    public Boolean isEmpty() {
        return KeyBinds.isEmpty();
    }

    // Set KeyBinds from file for persistence
    private void setFromFile(String filePath) {
        try {
            File file = new File(filePath);
            file.createNewFile(); // Does nothing if file exists already
        } catch (IOException e) {
            SafeTweaksClient.LOGGER.error("Error reading key binds: " + e.getMessage());
        }

        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String[] keyVal;
            for(String line = br.readLine(); line != null; line = br.readLine()) {
                keyVal = line.split("=");
                KeyBinds.put(keyVal[0], new KeyBinding(keyVal[0], InputUtil.Type.KEYSYM, Integer.parseInt(keyVal[1]), category));
            }
        } catch (IOException e) {
            SafeTweaksClient.LOGGER.error("Could not read key binds from file: " + e.getMessage());
        }
    }

    // Write KeyBinds to file in order to read on next init
    private static void writeToFile(String path, HashMap<String, KeyBinding> keyBinds) {
        try {
            File file = new File(path);
            file.createNewFile(); // Does nothing if file exists already

            PrintWriter pr = new PrintWriter(file);
            for (Map.Entry<String, KeyBinding> entry : keyBinds.entrySet()) {
                pr.println(entry.getKey() + "=" + entry.getValue().getDefaultKey().getCode());
            }
            pr.close();
        } catch (FileNotFoundException e) {
            SafeTweaksClient.LOGGER.error("Error writing key binds: " + e.getMessage());
        } catch (IOException e) {
            SafeTweaksClient.LOGGER.error("Error writing key binds: " + e.getMessage());
        }
    }

    // Wrapper for writing to file
    public static void saveBindsPersistent() {
        writeToFile(instance.filePath, instance.KeyBinds);
    }
}
