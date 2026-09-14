package farn.threeD_item.handler.apron;

import forge.ITextureProvider;
import io.github.fabriccompatibilitylayers.forge.stapi.client.ForgeStAPIClient;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.item.BlockItemForm;

public class ApronStapiHandler {

    public static int getForgeTextureIcon(ItemStack stack, int original) {
        Item item = stack.getItem();
        if(item instanceof ITextureProvider)
            return ForgeStAPIClient.getSpriteSheet(((ITextureProvider)item).getTextureFile())
                    .getTextureIndex(item.getAtlas(), original);
        else if (item instanceof BlockItemForm) {
            Block block = ((BlockItemForm)item).getBlock();

            if (block instanceof ITextureProvider) {
                return ForgeStAPIClient.getSpriteSheet(((ITextureProvider)block).getTextureFile())
                        .getTextureIndex(block.getAtlas(), original);
            }
        }
        return original;
    }
}
