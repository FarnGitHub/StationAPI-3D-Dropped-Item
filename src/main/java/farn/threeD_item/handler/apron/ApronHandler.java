package farn.threeD_item.handler.apron;

import forge.ForgeHooksClient;
import forge.ICustomItemRenderer;
import forge.MinecraftForgeClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.item.ItemStack;

public class ApronHandler {

    public static boolean shouldNotRenderCustomBlock(ItemStack itemStack, BlockRenderManager render, int meta, float brightness) {
        ICustomItemRenderer customRenderer = MinecraftForgeClient.getCustomItemRenderer(itemStack.itemId);
        if (customRenderer != null) {
            ForgeHooksClient.renderCustomItem(customRenderer, render, itemStack.itemId, meta, brightness);
            return false;
        }
        return true;
    }

    public static void bindTexture(Object obj) {
        ForgeHooksClient.overrideTexture(obj);
    }
}
