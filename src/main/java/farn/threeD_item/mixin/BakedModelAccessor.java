package farn.threeD_item.mixin;

import net.minecraft.client.render.Tessellator;
import net.modificationstation.stationapi.api.client.color.item.ItemColors;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.BakedModelRendererImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Random;

@Mixin(BakedModelRendererImpl.class)
public interface BakedModelAccessor {

    @Accessor("random")
    Random m3d_getRandom();

    @Invoker("colorF2I")
    int m3d_colorF2I(float r, float g, float b);

    @Accessor("tessellator")
    Tessellator m3d_get_tessellator();

    @Invoker("redI2F")
    float m3d_redI2F(int color);

    @Invoker("greenI2F")
    float m3d_greenI2F(int color);

    @Invoker("blueI2F")
    float m3d_blueI2F(int color);

    @Accessor("itemColors")
    ItemColors m3d_itemColors();
}
