package tterrag.potionapi.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBrewingStand;
import tterrag.potionapi.api.brewing.PotionUtil;

public class ContainerBetterBrewing extends ContainerBrewingStand
{
    public ContainerBetterBrewing(InventoryPlayer p_i1805_1_, TileEntityBrewingStand p_i1805_2_)
    {
        super(p_i1805_1_, p_i1805_2_);
        this.inventorySlots.clear();
        this.inventoryItemStacks.clear();
        this.addSlotToContainer(new Potion(p_i1805_1_.player, p_i1805_2_, 0, 56, 46));
        this.addSlotToContainer(new Potion(p_i1805_1_.player, p_i1805_2_, 1, 79, 53));
        this.addSlotToContainer(new Potion(p_i1805_1_.player, p_i1805_2_, 2, 102, 46));
        this.theSlot = this.addSlotToContainer(new Ingredient(p_i1805_2_, 3, 79, 17));
        int i;

        for (i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(p_i1805_1_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(p_i1805_1_, i, 8 + i * 18, 142));
        }
    }

    protected class Ingredient extends ContainerBrewingStand.Ingredient
    {
        public Ingredient(IInventory p_i1803_2_, int p_i1803_3_, int p_i1803_4_, int p_i1803_5_)
        {
            super(p_i1803_2_, p_i1803_3_, p_i1803_4_, p_i1803_5_);
        }

        @Override
        public boolean isItemValid(ItemStack p_75214_1_)
        {
            if (super.isItemValid(p_75214_1_))
            {
                return true;
            }
            else
            {
                for (int i = 0; i < 3; i++)
                {
                    ItemStack stack = ContainerBetterBrewing.this.tileBrewingStand.getStackInSlot(i);
                    if (stack != null && PotionUtil.isItemIngredient(stack, p_75214_1_))
                    {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    protected class Potion extends ContainerBrewingStand.Potion
    {
        public Potion(EntityPlayer p_i1804_1_, IInventory p_i1804_2_, int p_i1804_3_, int p_i1804_4_, int p_i1804_5_)
        {
            super(p_i1804_1_, p_i1804_2_, p_i1804_3_, p_i1804_4_, p_i1804_5_);
        }

        @Override
        public boolean isItemValid(ItemStack p_75214_1_)
        {
            if (super.isItemValid(p_75214_1_))
            {
                return true;
            }
            else
            {
                return PotionUtil.isItemBase(p_75214_1_);
            }
        }
    }
}
