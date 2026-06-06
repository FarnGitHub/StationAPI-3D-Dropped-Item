package farn.threeD_item.mixin.impl;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import farn.threeD_item.main.ValueHolder;
import farn.threeD_item.Item3D;
import farn.threeD_item.handler.stapi.StapiItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.texture.SpriteAtlasTexture;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicItemRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(ArsenicItemRenderer.class)
public class ArsenicItemRendererMixin {

    @Shadow @Final private ItemRenderer itemRenderer;

    @WrapMethod(method="renderVanilla")
    public void item3d_wrapVanillaRender(ItemEntity item, float x, float y, float z, float delta, ItemStack var10, float var11, float var12, byte renderedAmount, SpriteAtlasTexture atlas, Operation<Void> original) {
        if(ValueHolder.enabled)
            StapiItemRenderer.render(itemRenderer, item, x, y, z, delta, var10, var11, var12, renderedAmount, atlas);
        else
            original.call(item, x, y,z,delta, var10, var11, var12, renderedAmount, atlas);
    }

    @WrapOperation(method="renderModel", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glRotatef(FFFF)V", ordinal = 1))
    public void item3d_rotateJsonRender(float angle, float x, float y, float z, Operation<Void> original, @Local(ordinal = 0, argsOnly = true) ItemEntity item, @Local(ordinal = 4, argsOnly = true) float delta) {
        if(Item3D.rotateJsonItem) {
            if(ValueHolder.enabled)
                angle = (((float)item.age + delta) / 20.0F + item.initialRotationAngle) * 57.2957795131F;
            original.call(angle,x,y,z);
        }
    }

    @Inject(method="renderModel", at = @At("HEAD"))
    public void item3d_renderJsonHead(CallbackInfo ci) {
        ValueHolder.renderDroppedItem = ValueHolder.enabled;
    }

    @Inject(method="renderModel", at = @At("TAIL"))
    public void item3d_renderJsonTail(CallbackInfo ci) {
        ValueHolder.renderDroppedItem = false;
    }
}
