package farn.threeD_item.handler.apron;

import forge.ITextureProvider;
import io.github.fabriccompatibilitylayers.forge.stapi.client.ForgeStAPIClient;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.item.BlockItemForm;

public class ApronStapiHandler {

    public static int getForgeTextureIcon(ItemStack stack, int original) {
        if(stack.getItem() instanceof ITextureProvider provider)
            return ForgeStAPIClient.getSpriteSheet(provider.getTextureFile())
                    .getTextureIndex(stack.getItem().getAtlas(), original);
        else if (stack.getItem() instanceof BlockItemForm blockItem) {
            var block = blockItem.getBlock();

            if (block instanceof ITextureProvider provider) {
                return ForgeStAPIClient.getSpriteSheet(provider.getTextureFile())
                        .getTextureIndex(block.getAtlas(), original);
            }
        }
        return original;
    }
}
