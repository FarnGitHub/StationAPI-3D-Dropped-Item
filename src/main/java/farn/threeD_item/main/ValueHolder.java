package farn.threeD_item.main;

import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.util.Properties;

public class ValueHolder {
    public static boolean renderDroppedItem = true;
    public static boolean enabled = true;

    public static File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "dropped3dItem.txt");
    public static Properties props = new Properties();

    public static final boolean hasApron = FabricLoader.getInstance().isModLoaded("apron");

    private ValueHolder() {}

    static {
        if(configFile.exists()) {
            try {
                props.load(new FileReader(configFile));
                enabled = Boolean.parseBoolean(props.getProperty("enabled", "true"));
            } catch (Exception ignored) {
            }
        } else {
            saveConfig();
        }
    }

    public static void saveConfig() {
        try {
            props.setProperty("enabled", Boolean.toString(enabled));
            props.store(new FileWriter(configFile), "3D Dropped Item Config");
        } catch (IOException ignored) {
        }
    }

}
