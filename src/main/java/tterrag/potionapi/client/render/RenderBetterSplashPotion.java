package tterrag.potionapi.client.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import tterrag.potionapi.PotionAPI;
import tterrag.potionapi.api.item.IPotionItem;
import tterrag.potionapi.common.entity.EntityBetterSplashPotion;

public class RenderBetterSplashPotion extends RenderSnowball
{
    public RenderBetterSplashPotion()
    {
        super(PotionAPI.potionThrowable, 0);
    }

    @Override
    public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
    {
        ItemStack stack = ((EntityBetterSplashPotion) p_76986_1_).getPotion();
        if (stack != null)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef((float) p_76986_2_, (float) p_76986_4_, (float) p_76986_6_);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glScalef(0.5F, 0.5F, 0.5F);
            this.bindEntityTexture(p_76986_1_);
            Tessellator tessellator = Tessellator.instance;
            IPotionItem pot = (IPotionItem) stack.getItem();
            GL11.glPushMatrix();
            this.func_77026_a(tessellator, stack.getItem().getIcon(stack, 1));
            GL11.glPopMatrix();
            
            int color = pot.getPotion(stack).getColor(pot.getData(stack));
            float r = (float) (color >> 16 & 255) / 255f;
            float g = (float) (color >> 8 & 255) / 255f;
            float b = (float) (color & 255) / 255f;
            GL11.glColor3f(r, g, b);
            GL11.glPushMatrix();
            this.func_77026_a(tessellator, stack.getItem().getIcon(stack, 0));
            GL11.glPopMatrix();
            GL11.glPopMatrix();
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
        }
    }
}
