package farn.threeD_item.mixin.impl;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import farn.threeD_item.Dropped3DItem;
import farn.threeD_item.handler.VanillaItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemRenderer.class)
public class VanillaItemRendererMixin {

    @WrapMethod(method="render(Lnet/minecraft/entity/ItemEntity;DDDFF)V")
    public void render(ItemEntity ent, double x, double y, double z, float yaw, float pitch, Operation<Void> original) {
        if(Dropped3DItem.enabled)
            VanillaItemRenderer.render((ItemRenderer) (Object) this, ent, x, y, z, yaw, pitch);
        else
            original.call(ent, x, y, z, yaw, pitch);
    }
}
