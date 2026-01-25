package farn.threeD_item;

import farn.threeD_item.mixin.BakedModelAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.TorchBlock;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.render.model.BakedModel;
import net.modificationstation.stationapi.api.client.render.model.BakedQuad;
import net.modificationstation.stationapi.api.client.texture.Sprite;
import net.modificationstation.stationapi.api.client.texture.SpriteAtlasTexture;
import net.modificationstation.stationapi.api.item.BlockItemForm;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.MathHelper;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicItemRenderer;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.BakedModelRendererImpl;
import net.modificationstation.stationapi.mixin.arsenic.client.EntityRendererAccessor;
import net.modificationstation.stationapi.mixin.arsenic.client.ItemRendererAccessor;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

public class Item3D {

    static ArsenicItemRenderer renderer;
    static ItemRenderer itemRenderer;
    static ItemRendererAccessor itemRendererAccessor;
    static EntityRendererAccessor entityRendererAccessor;
    public static boolean renderDroppedItem = false;
    public static boolean rotateJsonItem = true;
    public static boolean gcapiEnabled = FabricLoader.getInstance().isModLoaded("gcapi3");

    public static void render3DVanilla(ItemEntity item, float x, float y, float z, float delta, ItemStack stack, float yOffset, float rotateOffset, byte renderedAmount, SpriteAtlasTexture atlas) {
        glPushMatrix();
        glTranslatef(x, y + yOffset, z);
        Block block;
        if (stack.getItem() instanceof BlockItemForm blockItemForm && BlockRenderManager.isSideLit((block = blockItemForm.getBlock()).getRenderType()) && !(blockItemForm.getBlock() instanceof TorchBlock)) {
            glRotatef(rotateOffset, 0.0F, 1.0F, 0.0F);
            atlas.bindTexture();
            float renderScale = 0.25F;
            glScalef(0.25F, 0.25F, 0.25F);

            for (int loopIndex = 0; loopIndex < renderedAmount; ++loopIndex) {
                glPushMatrix();
                if (loopIndex > 0) {
                    float spinX = (itemRendererAccessor.getRandom().nextFloat() * 2.0F - 1.0F) * 0.2F / renderScale;
                    float spinY = (itemRendererAccessor.getRandom().nextFloat() * 2.0F - 1.0F) * 0.2F / renderScale;
                    float spinZ = (itemRendererAccessor.getRandom().nextFloat() * 2.0F - 1.0F) * 0.2F / renderScale;
                    glTranslatef(spinX, spinY, spinZ);
                }

                itemRendererAccessor.getBlockRenderer().render(block, stack.getDamage(), item.getBrightnessAtEyes(delta));
                glPopMatrix();
            }
        } else {
            Sprite sprite = atlas.getSprite((stack.getItem()).getAtlas().getTexture(stack.getTextureId()).getId());
            float minU = sprite.getMinU();
            float maxU = sprite.getMaxU();
            float minV = sprite.getMinV();
            float maxV = sprite.getMaxV();
            float smallNudge = 1.0F / 16.0F;
            float bigNudge = 7.0F / 320.0F;
            int count = stack.count;
            byte mergeType;
            if(count < 2) {
                mergeType = 1;
            } else if(count < 16) {
                mergeType = 2;
            } else if(count < 32) {
                mergeType = 3;
            } else {
                mergeType = 4;
            }
            Tessellator tessellator = Tessellator.INSTANCE;
            glPushMatrix();
            GL11.glScalef(0.5F, 0.5F, 0.5F);
            glRotatef((((float)item.age + delta) / 20.0F + item.initialRotationAngle) * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            glTranslatef(-0.5F, -0.25F, -((smallNudge + bigNudge) * (float) mergeType / 2.0F));
            for(int loopIndex = 0; loopIndex < mergeType; ++loopIndex) {
                GL11.glTranslatef(0.0F, 0.0F, smallNudge + bigNudge);
                atlas.bindTexture();
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                renderItemModel(tessellator, maxU, minV, minU, maxV, atlas.getWidth(), atlas.getHeight(), smallNudge);
            }

            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
    }

    public static void unflatedBakedModelRenderer(BakedModel model, ItemStack stack, float brightness, BakedModelRendererImpl impl) {
        BakedModelAccessor accessor = (BakedModelAccessor)impl;
        accessor.m3d_getRandom().setSeed(42L);
        boolean bl = stack != null && stack.itemId != 0 && stack.count > 0;
        for (BakedQuad bakedQuad : model.getQuads(null, null, accessor.m3d_getRandom())) {
            int i = bl && bakedQuad.hasColor() ? accessor.m3d_itemColors().getColor(stack, bakedQuad.getColorIndex()) : -1;
            float light = MathHelper.lerp(bakedQuad.getEmission(), brightness, 1F);
            i = accessor.m3d_colorF2I(accessor.m3d_redI2F(i) * light, accessor.m3d_greenI2F(i) * light, accessor.m3d_blueI2F(i) * light);
            Direction face = bakedQuad.getFace();
            accessor.m3d_get_tessellator().quad(bakedQuad, 0, 0, 0, i, i, i, i, face.getOffsetX(), face.getOffsetY(), face.getOffsetZ(), false);
        }
    }

    public static void renderItemModel(Tessellator var0, float var1, float var2, float var3, float var4, int var5, int var6, float var7) {
        var0.startQuads();
        var0.normal(0.0F, 0.0F, 1.0F);
        var0.vertex(0.0D, 0.0D, 0.0D, (double)var1, (double)var4);
        var0.vertex(1.0D, 0.0D, 0.0D, (double)var3, (double)var4);
        var0.vertex(1.0D, 1.0D, 0.0D, (double)var3, (double)var2);
        var0.vertex(0.0D, 1.0D, 0.0D, (double)var1, (double)var2);
        var0.draw();
        var0.startQuads();
        var0.normal(0.0F, 0.0F, -1.0F);
        var0.vertex(0.0D, 1.0D, (double)(0.0F - var7), (double)var1, (double)var2);
        var0.vertex(1.0D, 1.0D, (double)(0.0F - var7), (double)var3, (double)var2);
        var0.vertex(1.0D, 0.0D, (double)(0.0F - var7), (double)var3, (double)var4);
        var0.vertex(0.0D, 0.0D, (double)(0.0F - var7), (double)var1, (double)var4);
        var0.draw();
        float var8 = (float)var5 * (var1 - var3);
        float var9 = (float)var6 * (var4 - var2);
        var0.startQuads();
        var0.normal(-1.0F, 0.0F, 0.0F);

        int var10;
        float var11;
        float var12;
        for(var10 = 0; (float)var10 < var8; ++var10) {
            var11 = (float)var10 / var8;
            var12 = var1 + (var3 - var1) * var11 - 0.5F / (float)var5;
            var0.vertex((double)var11, 0.0D, (double)(0.0F - var7), (double)var12, (double)var4);
            var0.vertex((double)var11, 0.0D, 0.0D, (double)var12, (double)var4);
            var0.vertex((double)var11, 1.0D, 0.0D, (double)var12, (double)var2);
            var0.vertex((double)var11, 1.0D, (double)(0.0F - var7), (double)var12, (double)var2);
        }

        var0.draw();
        var0.startQuads();
        var0.normal(1.0F, 0.0F, 0.0F);

        float var13;
        for(var10 = 0; (float)var10 < var8; ++var10) {
            var11 = (float)var10 / var8;
            var12 = var1 + (var3 - var1) * var11 - 0.5F / (float)var5;
            var13 = var11 + 1.0F / var8;
            var0.vertex((double)var13, 1.0D, (double)(0.0F - var7), (double)var12, (double)var2);
            var0.vertex((double)var13, 1.0D, 0.0D, (double)var12, (double)var2);
            var0.vertex((double)var13, 0.0D, 0.0D, (double)var12, (double)var4);
            var0.vertex((double)var13, 0.0D, (double)(0.0F - var7), (double)var12, (double)var4);
        }

        var0.draw();
        var0.startQuads();
        var0.normal(0.0F, 1.0F, 0.0F);

        for(var10 = 0; (float)var10 < var9; ++var10) {
            var11 = (float)var10 / var9;
            var12 = var4 + (var2 - var4) * var11 - 0.5F / (float)var6;
            var13 = var11 + 1.0F / var9;
            var0.vertex(0.0D, (double)var13, 0.0D, (double)var1, (double)var12);
            var0.vertex(1.0D, (double)var13, 0.0D, (double)var3, (double)var12);
            var0.vertex(1.0D, (double)var13, (double)(0.0F - var7), (double)var3, (double)var12);
            var0.vertex(0.0D, (double)var13, (double)(0.0F - var7), (double)var1, (double)var12);
        }

        var0.draw();
        var0.startQuads();
        var0.normal(0.0F, -1.0F, 0.0F);

        for(var10 = 0; (float)var10 < var9; ++var10) {
            var11 = (float)var10 / var9;
            var12 = var4 + (var2 - var4) * var11 - 0.5F / (float)var6;
            var0.vertex(1.0D, (double)var11, 0.0D, (double)var3, (double)var12);
            var0.vertex(0.0D, (double)var11, 0.0D, (double)var1, (double)var12);
            var0.vertex(0.0D, (double)var11, (double)(0.0F - var7), (double)var1, (double)var12);
            var0.vertex(1.0D, (double)var11, (double)(0.0F - var7), (double)var3, (double)var12);
        }

        var0.draw();
    }

    public static void setVariableItemRenderer(ArsenicItemRenderer rendererRaw, ItemRenderer itemRendererRaw, ItemRendererAccessor itemRendererAccessorRaw, EntityRendererAccessor entityRendererAccessorRaw) {
        renderer = rendererRaw;
        itemRenderer = itemRendererRaw;
        itemRendererAccessor = itemRendererAccessorRaw;
        entityRendererAccessor = entityRendererAccessorRaw;
    }

    public static boolean isEnabled() {
        if(gcapiEnabled) {
            return GCAPI_Handler.instance.enabled;
        }
        return true;
    }

}
