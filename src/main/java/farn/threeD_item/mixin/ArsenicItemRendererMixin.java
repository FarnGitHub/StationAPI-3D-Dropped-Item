package farn.threeD_item.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import farn.threeD_item.Item3D;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.render.model.BakedModel;
import net.modificationstation.stationapi.api.client.texture.SpriteAtlasTexture;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicItemRenderer;
import net.modificationstation.stationapi.mixin.arsenic.client.EntityRendererAccessor;
import net.modificationstation.stationapi.mixin.arsenic.client.ItemRendererAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArsenicItemRenderer.class)
public class ArsenicItemRendererMixin {

    @Shadow @Final private ItemRenderer itemRenderer;

    @Shadow @Final private ItemRendererAccessor itemRendererAccessor;

    @Shadow @Final private EntityRendererAccessor entityRendererAccessor;

    @WrapMethod(method="renderVanilla")
    public void overwriteArsenicVanillaRender(ItemEntity item, float x, float y, float z, float delta, ItemStack var10, float var11, float var12, byte renderedAmount, SpriteAtlasTexture atlas, Operation<Void> original) {
        if(Item3D.isEnabled()) {
            Item3D.setVariableItemRenderer((ArsenicItemRenderer) (Object)this, itemRenderer, itemRendererAccessor, entityRendererAccessor);
            Item3D.render3DVanilla(item, x, y,z,delta, var10, var11, var12, renderedAmount, atlas);
        } else {
            original.call(item, x, y,z,delta, var10, var11, var12, renderedAmount, atlas);
        }
    }

    @Inject(method="renderModel", at = @At("HEAD"))
    public void setRenderAs3DHead(ItemEntity item, float x, float y, float z, float delta, ItemStack var10, float var11, float var12, byte renderedAmount, SpriteAtlasTexture atlas, BakedModel model, CallbackInfo ci) {
        Item3D.renderDroppedItem = true;
    }

    @Inject(method="renderModel", at = @At("TAIL"))
    public void setRenderAs3DTail(ItemEntity item, float x, float y, float z, float delta, ItemStack var10, float var11, float var12, byte renderedAmount, SpriteAtlasTexture atlas, BakedModel model, CallbackInfo ci) {
        Item3D.renderDroppedItem = false;
    }

    @WrapOperation(method="renderModel", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glRotatef(FFFF)V", ordinal = 1))
    public void wrapRender3D(float angle, float x, float y, float z, Operation<Void> original, @Local(ordinal = 0, argsOnly = true) ItemEntity item, @Local(ordinal = 4, argsOnly = true) float delta) {
        if(Item3D.rotateJsonItem) {
            if(Item3D.isEnabled()) {
                original.call((((float)item.age + delta) / 20.0F + item.initialRotationAngle) * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            } else {
                original.call(angle,x,y,z);
            }
        }
    }
}
