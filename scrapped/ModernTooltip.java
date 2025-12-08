package farn.mcpatcher_custom_texture;

import farn.mcpatcher_custom_texture.mixin.DrawContextAccessor;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.mine_diver.unsafeevents.listener.ListenerPriority;
import net.minecraft.client.gui.DrawContext;
import net.modificationstation.stationapi.api.client.TooltipHelper;
import net.modificationstation.stationapi.api.client.event.gui.screen.container.TooltipRenderEvent;
import net.modificationstation.stationapi.mixin.item.client.HandledScreenAccessor;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.util.Null;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class ModernTooltip {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @Entrypoint.Logger
    public static Logger LOGGER = Null.get();

    private static final DrawContext CONTEXT = new DrawContext();
    public static boolean AMI = true;

    public static void drawTooltipBackground(int x, int y, int width, int height, int rgb, boolean customColor) {
        int bgColor = 0xFE000000;
        int borderStart = 0x505050FF;
        int borderEnd = (borderStart & 0x00FFFFFE) >> 1 | (borderStart & 0xFF000000);

        if(customColor) {
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;
            bgColor = (0xFE << 24) | (r / 3 << 16) | (g / 3 << 8) | (b / 3);
            borderStart = (0xFF << 24) | (Math.min(255, (int)(r * 0.7)) << 16) | (Math.min(255, (int)(g * 0.7)) << 8) | Math.min(255, (int)(b * 0.7));
            borderEnd = (0xFF << 24) | (r / 4 << 16) | (g / 4 << 8) | (b / 4);
        }

        fillGradient(x - 3, y - 4, x + width + 3, y - 3, bgColor, bgColor);
        fillGradient(x - 3, y + height + 3, x + width + 3, y + height + 4, bgColor, bgColor);
        fillGradient(x - 3, y - 3, x + width + 3, y + height + 3, bgColor, bgColor);
        fillGradient(x - 4, y - 3, x - 3, y + height + 3, bgColor, bgColor);
        fillGradient(x + width + 3, y - 3, x + width + 4, y + height + 3, bgColor, bgColor);

        fillGradient(x - 3, y - 2, x - 2, y + height + 2, borderStart, borderEnd);
        fillGradient(x + width + 2, y - 2, x + width + 3, y + height + 2, borderStart, borderEnd);
        fillGradient(x - 3, y - 3, x + width + 3, y - 2, borderStart, borderStart);
        fillGradient(x - 3, y + height + 2, x + width + 3, y + height + 3, borderEnd, borderEnd);
    }

    private static void fillGradient(int x1, int y1, int x2, int y2, int color1, int color2) {
        ((DrawContextAccessor) CONTEXT).invokeFillGradient(x1, y1, x2, y2, color1, color2);
    }
}
