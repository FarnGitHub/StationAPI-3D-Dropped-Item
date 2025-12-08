package farn.mcpatcher_custom_texture.mixin;

import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DrawContext.class)
public interface DrawContextAccessor {

    @Invoker("fillGradient")
    void invokeFillGradient(int startX, int startY, int endX, int endY, int colorStart, int colorEnd);
}
