package farn.mcpatcher_custom_texture.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import farn.mcpatcher_custom_texture.ModernTooltip;
import net.glasslauncher.mods.alwaysmoreitems.gui.AMIDrawContext;
import net.glasslauncher.mods.alwaysmoreitems.gui.Tooltip;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Tooltip.class)
public class AMIToolTipMixin {

    @WrapOperation(method="renderBackground", at = @At(value = "INVOKE", target = "Lnet/glasslauncher/mods/alwaysmoreitems/gui/AMIDrawContext;fill(IIIII)V"))
    public void renderBackGroundTooltipCustom(AMIDrawContext instance, int x1, int y1, int x2, int y2, int color, Operation<Void> original, @Local(index=1) Tooltip.Bounds bounds, @Local(index=2) boolean header) {
        ModernTooltip.drawTooltipBackground(x1 + 3, y1 + 3, x2 - x1 - 3, y2 - y1 - 3, color, header);
    }

    @Inject(method="<clinit>", at = @At("HEAD"))
    private static void hasAMITrue(CallbackInfo ci) {

    }

}
