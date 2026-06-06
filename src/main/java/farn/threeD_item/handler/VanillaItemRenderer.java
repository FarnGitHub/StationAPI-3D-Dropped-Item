package farn.threeD_item.handler;

import farn.threeD_item.main.ValueHolder;
import farn.threeD_item.handler.apron.ApronHandler;
import net.minecraft.block.Block;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class VanillaItemRenderer {

    @SuppressWarnings("unused")
    public static void render(ItemRenderer renderer, ItemEntity ent, double x, double y, double z, float pitch, float yaw) {
        renderer.random.setSeed(187L);
        ItemStack stack = ent.stack;
        if(stack.getItem() != null) {
            GL11.glPushMatrix();
            float yOffset = MathHelper.sin(((float)ent.age + yaw) / 10.0F + ent.initialRotationAngle) * 0.1F + 0.1F;
            float rotation = (((float)ent.age + yaw) / 20.0F + ent.initialRotationAngle) * 57.295776F;
            byte mergeType;
            if(stack.count < 2) {
                mergeType = 1;
            } else if(stack.count < 16) {
                mergeType = 2;
            } else if(stack.count < 32) {
                mergeType = 3;
            } else {
                mergeType = 4;
            }

            GL11.glTranslatef((float)x, (float)y + yOffset, (float)z);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            Block block = null;
            if(stack.itemId < 256) {
                block = Block.BLOCKS[stack.itemId];
            }
            if(block != null && BlockRenderManager.isSideLit(block.getRenderType())) {
                GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
                renderer.bindTexture("/terrain.png");
                if(ValueHolder.hasApron)
                    ApronHandler.bindTexture(block);
                float size = 0.25F;
                int render = block.getRenderType();
                if(render == 1 || render == 19 || render == 12 || render == 2) {
                    size = 0.5F;
                }

                GL11.glScalef(size, size, size);

                for(int loop = 0; loop < mergeType; ++loop) {
                    GL11.glPushMatrix();
                    if(loop > 0) {
                        float xOff = (renderer.random.nextFloat() * 2.0F - 1.0F) * 0.2F / size;
                        float yOff = (renderer.random.nextFloat() * 2.0F - 1.0F) * 0.2F / size;
                        float zOff = (renderer.random.nextFloat() * 2.0F - 1.0F) * 0.2F / size;
                        GL11.glTranslatef(xOff, yOff, zOff);
                    }

                    float brightness = ent.getBrightnessAtEyes(1.0F);
                    if(!ValueHolder.hasApron || ApronHandler.shouldNotRenderCustomBlock(stack, renderer.blockRenderer, stack.getDamage(), brightness))
                        renderer.blockRenderer.render(block, stack.getDamage(), brightness);
                    GL11.glPopMatrix();
                }
            } else {
                int textureId = stack.getTextureId();
                GL11.glScalef(0.5F, 0.5F, 0.5F);

                if(renderer.useCustomDisplayColor) {
                    int color = Item.ITEMS[stack.itemId].getColorMultiplier(0);
                    float r = (float)(color >> 16 & 255) / 255.0F;
                    float g = (float)(color >> 8 & 255) / 255.0F;
                    float b = (float)(color & 255) / 255.0F;
                    float brightness = ent.getBrightnessAtEyes(1.0F);
                    renderDroppedItem(renderer, ent, textureId, mergeType, block, r * brightness, g * brightness, b * brightness, rotation);
                } else {
                    renderDroppedItem(renderer, ent, textureId, mergeType, block, 1.0F, 1.0F, 1.0F, rotation);
                }
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
        }
    }

    private static void renderDroppedItem(ItemRenderer renderer, ItemEntity ent, int texture, int mergeType, Block block, float r, float g, float b, float rotation) {
        Tessellator tess = Tessellator.INSTANCE;
        float minU = (float)(texture % 16 * 16) / 256.0F;
        float maxU = (float)(texture % 16 * 16 + 16) / 256.0F;
        float minV = (float)(texture / 16 * 16) / 256.0F;
        float maxV = (float)(texture / 16 * 16 + 16) / 256.0F;
        GL11.glPushMatrix();
        GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
        ItemStack stack = ent.stack;
        GL11.glTranslatef(-0.5F, -0.25F, -(0.084375F * (float)mergeType / 2.0F));
        for(int loop = 0; loop < mergeType; ++loop) {
            GL11.glTranslatef(0.0F, 0.0F, 0.084375F);
            if(block != null) {
                renderer.bindTexture("/terrain.png");
                if(ValueHolder.hasApron)
                    ApronHandler.bindTexture(block);
            } else {
                renderer.bindTexture("/gui/items.png");
                if(ValueHolder.hasApron)
                    ApronHandler.bindTexture(Item.ITEMS[stack.itemId]);
            }

            GL11.glColor4f(r, g, b, 1.0F);
            draw3DItemQuad(tess, maxU, minV, minU, maxV,0.0625F);
        }

        GL11.glPopMatrix();

    }

    public static void draw3DItemQuad(Tessellator tess, float maxU, float minV, float minU, float maxV, float bleed) {
        float f6 = 1.0F;
        tess.startQuads();
        tess.normal(0.0F, 0.0F, 1.0F);
        tess.vertex(0.0D, 0.0D, 0.0D, maxU, maxV);
        tess.vertex(f6, 0.0D, 0.0D, minU, maxV);
        tess.vertex(f6, 1.0D, 0.0D, minU, minV);
        tess.vertex(0.0D, 1.0D, 0.0D, maxU, minV);
        tess.draw();
        tess.startQuads();
        tess.normal(0.0F, 0.0F, -1.0F);
        tess.vertex(0.0D, 1.0D, 0.0F - bleed, maxU, minV);
        tess.vertex(f6, 1.0D, 0.0F - bleed, minU, minV);
        tess.vertex(f6, 0.0D, 0.0F - bleed, minU, maxV);
        tess.vertex(0.0D, 0.0D, 0.0F - bleed, maxU, maxV);
        tess.draw();
        tess.startQuads();
        tess.normal(-1.0F, 0.0F, 0.0F);

        int i7;
        float f8;
        float f9;
        float f10;
        for(i7 = 0; i7 < 16; ++i7) {
            f8 = (float)i7 / 16.0F;
            f9 = maxU + (minU - maxU) * f8 - 0.001953125F;
            f10 = f6 * f8;
            tess.vertex(f10, 0.0D, 0.0F - bleed, f9, maxV);
            tess.vertex(f10, 0.0D, 0.0D, f9, maxV);
            tess.vertex(f10, 1.0D, 0.0D, f9, minV);
            tess.vertex(f10, 1.0D, 0.0F - bleed, f9, minV);
        }

        tess.draw();
        tess.startQuads();
        tess.normal(1.0F, 0.0F, 0.0F);

        for(i7 = 0; i7 < 16; ++i7) {
            f8 = (float)i7 / 16.0F;
            f9 = maxU + (minU - maxU) * f8 - 0.001953125F;
            f10 = f6 * f8 + 0.0625F;
            tess.vertex(f10, 1.0D, 0.0F - bleed, f9, minV);
            tess.vertex(f10, 1.0D, 0.0D, f9, minV);
            tess.vertex(f10, 0.0D, 0.0D, f9, maxV);
            tess.vertex(f10, 0.0D, 0.0F - bleed, f9, maxV);
        }

        tess.draw();
        tess.startQuads();
        tess.normal(0.0F, 1.0F, 0.0F);

        for(i7 = 0; i7 < 16; ++i7) {
            f8 = (float)i7 / 16.0F;
            f9 = maxV + (minV - maxV) * f8 - 0.001953125F;
            f10 = f6 * f8 + 0.0625F;
            tess.vertex(0.0D, f10, 0.0D, maxU, f9);
            tess.vertex(f6, f10, 0.0D, minU, f9);
            tess.vertex(f6, f10, 0.0F - bleed, minU, f9);
            tess.vertex(0.0D, f10, 0.0F - bleed, maxU, f9);
        }

        tess.draw();
        tess.startQuads();
        tess.normal(0.0F, -1.0F, 0.0F);

        for(i7 = 0; i7 < 16; ++i7) {
            f8 = (float)i7 / 16.0F;
            f9 = maxV + (minV - maxV) * f8 - 0.001953125F;
            f10 = f6 * f8;
            tess.vertex(f6, f10, 0.0D, minU, f9);
            tess.vertex(0.0D, f10, 0.0D, maxU, f9);
            tess.vertex(0.0D, f10, 0.0F - bleed, maxU, f9);
            tess.vertex(f6, f10, 0.0F - bleed, minU, f9);
        }

        tess.draw();
    }

}
