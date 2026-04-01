package farn.threeD_item.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import farn.threeD_item.Item3D;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.render.model.BakedModel;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.BakedModelRendererImpl;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BakedModelRendererImpl.class)
public abstract class BakedModelRendererMixin {

    @WrapMethod(method="renderBakedItemModelFlat")
    public void threeDfiedJsonModel(BakedModel model, ItemStack stack, float brightness, Operation<Void> original) {
        if(Item3D.isEnabled())
            Item3D.render3DGroundModel(model, stack, brightness, (BakedModelRendererImpl) (Object) this);
        else
            original.call(model, stack, brightness);
    }
}
