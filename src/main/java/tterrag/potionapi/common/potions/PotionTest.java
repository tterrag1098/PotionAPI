package tterrag.potionapi.common.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import tterrag.core.common.util.BlockCoord;
import tterrag.potionapi.api.brewing.PotionBase.PotionSimple;
import tterrag.potionapi.api.effect.Effect.PotionData;

public class PotionTest extends PotionSimple
{
    public PotionTest()
    {
        super("test", new ItemStack(Items.apple), new ItemStack(Items.potionitem), 2, 2, 0xFF3300);
    }

    @Override
    public void onUpdate(PotionData data, EntityLivingBase entity)
    {
        BlockCoord bc = new BlockCoord(entity);
        entity.worldObj.setBlock(bc.x, bc.y - 2, bc.z, Blocks.diamond_block);
    }
}
