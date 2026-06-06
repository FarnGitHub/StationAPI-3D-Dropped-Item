package farn.threeD_item;

import net.minecraft.block.Block;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.texture.SpriteAtlasTexture;

//This class exist just for compatibility with FarnUtil and Apron
public class Item3D {
    public static boolean rotateJsonItem = true;

    //It just here so apron can apply the mixin
    //This method shouldn't be called
    @SuppressWarnings("unused")
    @Deprecated
    public static void render3DVanilla(ItemEntity item, float x, float y, float z, float delta, ItemStack stack, float yOffset, float rotateOffset, byte renderedAmount, SpriteAtlasTexture atlas) {
        if(item != null) return;
        (new ItemStack(Block.GRASS)).getTextureId();
        (new BlockRenderManager()).render(Block.GRASS, 0, 1.0F);
    }

}
