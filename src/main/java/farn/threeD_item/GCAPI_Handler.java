package farn.threeD_item;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;
import net.glasslauncher.mods.gcapi3.api.ConfigRoot;

public class GCAPI_Handler {

    @ConfigRoot(value = "item3d_config", visibleName = "3D Dropped item Config")
    public static final InstanceConfig instance = new InstanceConfig();

    public static class InstanceConfig {

        @ConfigEntry(name = "Enabled")
        public Boolean enabled = true;

    }
}
