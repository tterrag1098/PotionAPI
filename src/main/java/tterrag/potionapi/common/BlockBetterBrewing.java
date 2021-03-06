package tterrag.potionapi.common;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tterrag.potionapi.PotionAPI;

public class BlockBetterBrewing extends BlockBrewingStand
{
    public BlockBetterBrewing()
    {
        super();
        setHardness(0.5F);
        setLightLevel(0.125F);
        setBlockName("brewingStand");
        setBlockTextureName("brewing_stand");
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
        return new TileBetterBrewing();
    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float p_149727_7_, float p_149727_8_,
            float p_149727_9_)
    {
        player.openGui(PotionAPI.INSTANCE, 0, world, x, y, z);
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int p_149749_6_)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileBetterBrewing)
        {
            TileBetterBrewing stand = (TileBetterBrewing) te;
            for (int i = 0; i < stand.getSizeInventory(); i++)
            {
                ItemStack stack = stand.getStackInSlot(i);
                if (stack != null)
                {
                    dropBlockAsItem(world, x, y, z, stack);
                }
            }
        }
        world.removeTileEntity(x, y, z);
    }
}