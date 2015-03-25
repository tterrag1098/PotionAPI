package tterrag.potionapi.common;

import net.minecraft.block.BlockBrewingStand;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BetterBrewingStand extends BlockBrewingStand
{
    public BetterBrewingStand()
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
}