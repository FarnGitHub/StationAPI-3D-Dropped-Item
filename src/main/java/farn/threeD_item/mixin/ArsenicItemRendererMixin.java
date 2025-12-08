package farn.threeD_item.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import farn.threeD_item.Item3D;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.texture.SpriteAtlasTexture;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicItemRenderer;
import net.modificationstation.stationapi.mixin.arsenic.client.EntityRendererAccessor;
import net.modificationstation.stationapi.mixin.arsenic.client.ItemRendererAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

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
}
