package farn.threeD_item.mixin.impl;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import farn.threeD_item.Dropped3DItem;
import net.modificationstation.stationapi.api.client.render.model.json.ModelTransformation;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.BakedModelRendererImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(BakedModelRendererImpl.class)
public abstract class BakedModelRendererMixin {

    @Definition(id="renderMode", local = @Local(argsOnly = true, type=ModelTransformation.Mode.class))
    @Definition(id="GROUND", field = "Lnet/modificationstation/stationapi/api/client/render/model/json/ModelTransformation$Mode;GROUND:Lnet/modificationstation/stationapi/api/client/render/model/json/ModelTransformation$Mode;")
    @Expression("renderMode == GROUND")
    @WrapOperation(method="renderItem(Lnet/minecraft/item/ItemStack;Lnet/modificationstation/stationapi/api/client/render/model/json/ModelTransformation$Mode;FLnet/modificationstation/stationapi/api/client/render/model/BakedModel;)V", at = @At("MIXINEXTRAS:EXPRESSION"))
    public boolean item3d_ForceRenderAs3D(Object left, Object right, Operation<Boolean> original) {
        return !Dropped3DItem.renderDroppedItem && original.call(left, right);
    }
}
