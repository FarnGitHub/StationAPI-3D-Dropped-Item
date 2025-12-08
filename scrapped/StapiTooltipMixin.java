package farn.mcpatcher_custom_texture.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import farn.mcpatcher_custom_texture.ModernTooltip;
import net.modificationstation.stationapi.impl.client.gui.screen.container.CustomTooltipRendererImpl;
import net.modificationstation.stationapi.mixin.item.client.DrawContextAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CustomTooltipRendererImpl.class)
public class StapiTooltipMixin {

    @WrapOperation(method="lambda$renderCustomTooltips$1", at = @At(value="INVOKE", target = "Lnet/modificationstation/stationapi/mixin/item/client/DrawContextAccessor;invokeFill(IIIII)V"))
    private static void redirectFill(DrawContextAccessor instance, int x1, int y1, int x2, int y2, int color, Operation<Void> original) {
        if(!ModernTooltip.AMI) {
            ModernTooltip.drawTooltipBackground(x1 + 3, y1 + 3, x2 - x1 - 3, y2 - y1 - 3, color, false);
        }
    }
}
