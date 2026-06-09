package farn.threeD_item.handler;

import farn.threeD_item.Dropped3DItem;
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
            if(stack.itemId < 256)
                block = Block.BLOCKS[stack.itemId];

            if(block != null && BlockRenderManager.isSideLit(block.getRenderType())) {
                GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
                renderer.bindTexture("/terrain.png");
                if(Dropped3DItem.apron)
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
                    if(!Dropped3DItem.apron || ApronHandler.skipVanillaBlockRender(stack, renderer.blockRenderer, stack.getDamage(), brightness))
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
                    renderDroppedItem(renderer, stack, textureId, mergeType, block, r * brightness, g * brightness, b * brightness, rotation);
                } else {
                    renderDroppedItem(renderer, stack, textureId, mergeType, block, 1.0F, 1.0F, 1.0F, rotation);
                }
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
        }
    }

    private static void renderDroppedItem(ItemRenderer renderer, ItemStack stack, int texture, int mergeType, Block block, float r, float g, float b, float rotation) {
        Tessellator tess = Tessellator.INSTANCE;
        float minU = (float)(texture % 16 * 16) / 256.0F;
        float maxU = (float)(texture % 16 * 16 + 16) / 256.0F;
        float minV = (float)(texture / 16 * 16) / 256.0F;
        float maxV = (float)(texture / 16 * 16 + 16) / 256.0F;
        GL11.glPushMatrix();
        GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.25F, -(0.084375F * (float)mergeType / 2.0F));
        for(int loop = 0; loop < mergeType; ++loop) {
            GL11.glTranslatef(0.0F, 0.0F, 0.084375F);
            renderer.bindTexture(
                    block != null ? "/terrain.png" : "/gui/items.png"
            );
            if(Dropped3DItem.apron)
                ApronHandler.bindTexture(
                        block != null ? block : Item.ITEMS[stack.itemId]
                );

            GL11.glColor4f(r, g, b, 1.0F);
            render3DItem(tess, maxU, minV, minU, maxV, 256, 256,0.0625F);
        }

        GL11.glPopMatrix();

    }

    public static void render3DItem(
            Tessellator tess,
            float maxU,
            float minV,
            float minU,
            float maxV,
            int atlasWidth,
            int atlasHeight,
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
        float width = (float)atlasWidth * (maxU - minU);
        float height = (float)atlasHeight * (maxV - minV);
        tess.startQuads();
        tess.normal(-1.0F, 0.0F, 0.0F);

        for(int column = 0; (float)column < width; ++column) {
            float normal = (float)column / width;
            float interpolate = maxU + (minU - maxU) * normal - 0.5F / (float)atlasWidth;
            tess.vertex(normal, 0.0D, (0.0F - bleed), interpolate, maxV);
            tess.vertex(normal, 0.0D, 0.0D, interpolate, maxV);
            tess.vertex(normal, 1.0D, 0.0D, interpolate, minV);
            tess.vertex(normal, 1.0D, (0.0F - bleed), interpolate, minV);
        }

        tess.draw();
        tess.startQuads();
        tess.normal(1.0F, 0.0F, 0.0F);

        for(int column = 0; (float)column < width; ++column) {
            float normal = (float)column / width;
            float interpolate = maxU + (minU - maxU) * normal - 0.5F / (float)atlasWidth;
            float normal2 = normal + 1.0F / width;
            tess.vertex(normal2, 1.0D, (0.0F - bleed), interpolate, minV);
            tess.vertex(normal2, 1.0D, 0.0D, interpolate, minV);
            tess.vertex(normal2, 0.0D, 0.0D, interpolate, maxV);
            tess.vertex(normal2, 0.0D, (0.0F - bleed), interpolate, maxV);
        }

        tess.draw();
        tess.startQuads();
        tess.normal(0.0F, 1.0F, 0.0F);

        for(int column = 0; (float)column < height; ++column) {
            float normal = (float)column / height;
            float interpolate = maxV + (minV - maxV) * normal - 0.5F / (float)atlasHeight;
            float normal2 = normal + 1.0F / height;
            tess.vertex(0.0D, normal2, 0.0D, maxU, interpolate);
            tess.vertex(1.0D, normal2, 0.0D, minU, interpolate);
            tess.vertex(1.0D, normal2, (0.0F - bleed), minU, interpolate);
            tess.vertex(0.0D, normal2, (0.0F - bleed), maxU, interpolate);
        }

        tess.draw();
        tess.startQuads();
        tess.normal(0.0F, -1.0F, 0.0F);

        for(int column = 0; (float)column < height; ++column) {
            float normal = (float)column / height;
            float interpolate = maxV + (minV - maxV) * normal - 0.5F / (float)atlasHeight;
            tess.vertex(1.0D, normal, 0.0D, minU, interpolate);
            tess.vertex(0.0D, normal, 0.0D, maxU, interpolate);
            tess.vertex(0.0D, normal, (0.0F - bleed), maxU, interpolate);
            tess.vertex(1.0D, normal, (0.0F - bleed), minU, interpolate);
        }

        tess.draw();
    }

}
