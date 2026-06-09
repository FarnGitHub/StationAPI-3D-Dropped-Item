package farn.threeD_item.handler.stapi;

import farn.threeD_item.Dropped3DItem;
import farn.threeD_item.handler.VanillaItemRenderer;
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
        if (stack.getItem() instanceof BlockItemForm blockI && BlockRenderManager.isSideLit((block = blockI.getBlock()).getRenderType())) {
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

                if(!Dropped3DItem.apron || ApronHandler.skipVanillaBlockRender(stack, itemRenderer.blockRenderer, stack.getDamage(), item.getBrightnessAtEyes(delta)))
                    itemRenderer.blockRenderer.render(block, stack.getDamage(), item.getBrightnessAtEyes(delta));
                glPopMatrix();
            }
        } else if(stack.getItem() != null) {
            int textureId = stack.getTextureId();
            if(Dropped3DItem.apron)
                textureId = ApronStapiHandler.getForgeTextureIcon(stack, textureId);
            Sprite sprite = atlas.getSprite((stack.getItem()).getAtlas().getTexture(textureId).getId());
            float minU = sprite.getMinU();
            float maxU = sprite.getMaxU();
            float minV = sprite.getMinV();
            float maxV = sprite.getMaxV();
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
            glRotatef((((float)item.age + delta) / 20.0F + item.initialRotationAngle) * 57.295776F, 0.0F, 1.0F, 0.0F);
            glTranslatef(-0.5F, -0.25F, -(0.084375F * (float) mergeType / 2.0F));
            atlas.bindTexture();
            for(int loopIndex = 0; loopIndex < mergeType; ++loopIndex) {
                glTranslatef(0.0F, 0.0F, 0.084375F);
                VanillaItemRenderer.render3DItem(tessellator, maxU, minV, minU, maxV, atlas.getWidth(), atlas.getHeight(), 0.0625F);
            }

            glPopMatrix();
        }
        glPopMatrix();
    }
}
