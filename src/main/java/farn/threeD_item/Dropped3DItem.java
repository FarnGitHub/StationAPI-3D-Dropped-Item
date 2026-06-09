package farn.threeD_item;

import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

public class Dropped3DItem {
    public static boolean renderDroppedItem = true;
    public static boolean enabled = true;

    public static File cFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "3D_dropped_item.cfg");

    public static final boolean apron = FabricLoader.getInstance().isModLoaded("apron");

    private Dropped3DItem() {}

    static {
        if(cFile.exists()) {
            try(BufferedReader reader = new BufferedReader(new FileReader(cFile))) {
                enabled = reader.readLine().equalsIgnoreCase("true");
            } catch (Exception ignored) {
            }
        } else saveConfig();
    }

    public static void saveConfig() {
        try(PrintWriter writer = new PrintWriter(cFile)) {
            writer.println(enabled);
        } catch (Exception ignored) {
        }
    }

}
