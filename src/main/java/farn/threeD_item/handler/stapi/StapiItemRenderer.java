package farn.threeD_item.handler.stapi;

import farn.threeD_item.main.ValueHolder;
import farn.threeD_item.handler.apron.ApronStapiHandler;
import farn.threeD_item.handler.apron.ApronHandler;
import net.minecraft.block.Block;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.texture.Sprite;
import net.modificationstation.stationapi.api.client.texture.SpriteAtlasTexture;
import net.modificationstation.stationapi.api.item.BlockItemForm;

import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

public class StapiItemRenderer {

    public static void render(
            ItemRenderer itemRenderer,
            ItemEntity item,
            float x, float y, float z,
            float delta,
            ItemStack stack,
            float yOffset,
            float rotateOffset,
            byte renderedAmount,
            SpriteAtlasTexture atlas
    ) {
        glPushMatrix();
        glTranslatef(x, y + yOffset, z);
        Block block;
        if (stack.getItem() instanceof BlockItemForm blockItemForm && BlockRenderManager.isSideLit((block = blockItemForm.getBlock()).getRenderType())) {
            glRotatef(rotateOffset, 0.0F, 1.0F, 0.0F);
            atlas.bindTexture();
            float renderScale = 0.25F;
            glScalef(0.25F, 0.25F, 0.25F);

            for (int loopIndex = 0; loopIndex < renderedAmount; ++loopIndex) {
                glPushMatrix();
                if (loopIndex > 0) {
                    float spinX = (itemRenderer.random.nextFloat() * 2.0F - 1.0F) * 0.2F / renderScale;
                    float spinY = (itemRenderer.random.nextFloat() * 2.0F - 1.0F) * 0.2F / renderScale;
                    float spinZ = (itemRenderer.random.nextFloat() * 2.0F - 1.0F) * 0.2F / renderScale;
                    glTranslatef(spinX, spinY, spinZ);
                }

                if(!ValueHolder.hasApron || ApronHandler.skipVanillaBlockRender(stack, itemRenderer.blockRenderer, stack.getDamage(), item.getBrightnessAtEyes(delta)))
                    itemRenderer.blockRenderer.render(block, stack.getDamage(), item.getBrightnessAtEyes(delta));
                glPopMatrix();
            }
        } else {
            int textureId = stack.getTextureId();
            if(ValueHolder.hasApron) {
                textureId = ApronStapiHandler.getForgeTextureIcon(stack, textureId);
            }
            Sprite sprite = atlas.getSprite((stack.getItem()).getAtlas().getTexture(textureId).getId());
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

            if (itemRenderer.useCustomDisplayColor) {
                int rgb = Item.ITEMS[stack.itemId].getColorMultiplier(stack.getDamage());
                float r = (float) ((rgb >> 16) & 255) / 255.0F;
                float g = (float) ((rgb >> 8) & 255) / 255.0F;
                float b = (float) (rgb & 255) / 255.0F;
                float brightness = item.getBrightnessAtEyes(delta);
                glColor4f(r * brightness, g * brightness, b * brightness, 1.0F);
            }

            Tessellator tessellator = Tessellator.INSTANCE;
            glPushMatrix();
            glScalef(0.5F, 0.5F, 0.5F);
            glRotatef((((float)item.age + delta) / 20.0F + item.initialRotationAngle) * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            glTranslatef(-0.5F, -0.25F, -((smallNudge + bigNudge) * (float) mergeType / 2.0F));
            atlas.bindTexture();
            for(int loopIndex = 0; loopIndex < mergeType; ++loopIndex) {
                glTranslatef(0.0F, 0.0F, smallNudge + bigNudge);
                draw3DItemQuad(tessellator, maxU, minV, minU, maxV, atlas.getWidth(), atlas.getHeight(), smallNudge);
            }

            glPopMatrix();
        }
        glPopMatrix();
    }

    public static void draw3DItemQuad(
            Tessellator tess,
            float maxU,
            float minV,
            float minU,
            float maxV,
            int width,
            int height,
            float bleed
    ) {
        tess.startQuads();
        tess.normal(0.0F, 0.0F, 1.0F);
        tess.vertex(0.0D, 0.0D, 0.0D, maxU, maxV);
        tess.vertex(1.0D, 0.0D, 0.0D, minU, maxV);
        tess.vertex(1.0D, 1.0D, 0.0D, minU, minV);
        tess.vertex(0.0D, 1.0D, 0.0D, maxU, minV);
        tess.draw();
        tess.startQuads();
        tess.normal(0.0F, 0.0F, -1.0F);
        tess.vertex(0.0D, 1.0D, (0.0F - bleed), maxU, minV);
        tess.vertex(1.0D, 1.0D, (0.0F - bleed), minU, minV);
        tess.vertex(1.0D, 0.0D, (0.0F - bleed), minU, maxV);
        tess.vertex(0.0D, 0.0D, (0.0F - bleed), maxU, maxV);
        tess.draw();
        float var8 = (float)width * (maxU - minU);
        float var9 = (float)height * (maxV - minV);
        tess.startQuads();
        tess.normal(-1.0F, 0.0F, 0.0F);

        int var10;
        float var11;
        float var12;
        for(var10 = 0; (float)var10 < var8; ++var10) {
            var11 = (float)var10 / var8;
            var12 = maxU + (minU - maxU) * var11 - 0.5F / (float)width;
            tess.vertex(var11, 0.0D, (0.0F - bleed), var12, maxV);
            tess.vertex(var11, 0.0D, 0.0D, var12, maxV);
            tess.vertex(var11, 1.0D, 0.0D, var12, minV);
            tess.vertex(var11, 1.0D, (0.0F - bleed), var12, minV);
        }

        tess.draw();
        tess.startQuads();
        tess.normal(1.0F, 0.0F, 0.0F);

        float var13;
        for(var10 = 0; (float)var10 < var8; ++var10) {
            var11 = (float)var10 / var8;
            var12 = maxU + (minU - maxU) * var11 - 0.5F / (float)width;
            var13 = var11 + 1.0F / var8;
            tess.vertex(var13, 1.0D, (0.0F - bleed), var12, minV);
            tess.vertex(var13, 1.0D, 0.0D, var12, minV);
            tess.vertex(var13, 0.0D, 0.0D, var12, maxV);
            tess.vertex(var13, 0.0D, (0.0F - bleed), var12, maxV);
        }

        tess.draw();
        tess.startQuads();
        tess.normal(0.0F, 1.0F, 0.0F);

        for(var10 = 0; (float)var10 < var9; ++var10) {
            var11 = (float)var10 / var9;
            var12 = maxV + (minV - maxV) * var11 - 0.5F / (float)height;
            var13 = var11 + 1.0F / var9;
            tess.vertex(0.0D, var13, 0.0D, maxU, var12);
            tess.vertex(1.0D, var13, 0.0D, minU, var12);
            tess.vertex(1.0D, var13, (0.0F - bleed), minU, var12);
            tess.vertex(0.0D, var13, (0.0F - bleed), maxU, var12);
        }

        tess.draw();
        tess.startQuads();
        tess.normal(0.0F, -1.0F, 0.0F);

        for(var10 = 0; (float)var10 < var9; ++var10) {
            var11 = (float)var10 / var9;
            var12 = maxV + (minV - maxV) * var11 - 0.5F / (float)height;
            tess.vertex(1.0D, var11, 0.0D, minU, var12);
            tess.vertex(0.0D, var11, 0.0D, maxU, var12);
            tess.vertex(0.0D, var11, (0.0F - bleed), maxU, var12);
            tess.vertex(1.0D, var11, (0.0F - bleed), minU, var12);
        }

        tess.draw();
    }
}
