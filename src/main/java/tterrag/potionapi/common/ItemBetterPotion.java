package tterrag.potionapi.common;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import tterrag.potionapi.PotionAPI;
import tterrag.potionapi.api.brewing.IPotion;
import tterrag.potionapi.api.brewing.PotionUtil;
import tterrag.potionapi.api.item.IPotionItem;
import tterrag.potionapi.common.brewing.PotionRegistry;
import tterrag.potionapi.common.util.NBTUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBetterPotion extends Item implements IPotionItem
{
    @SideOnly(Side.CLIENT)
    private IIcon potionIcon;
    @SideOnly(Side.CLIENT)
    private IIcon splashIcon;
    @SideOnly(Side.CLIENT)
    private IIcon colorIcon;

    public ItemBetterPotion()
    {
        super();
        setUnlocalizedName("potionapi.potion");
        setCreativeTab(CreativeTabs.tabBrewing);
    }

    @Override
    public void registerIcons(IIconRegister register)
    {
        this.potionIcon = register.registerIcon(PotionAPI.MODID + ":potionbase");
        this.splashIcon = register.registerIcon(PotionAPI.MODID + ":potionsplash");
        this.colorIcon = register.registerIcon(PotionAPI.MODID + ":potioncolor");
    }

    @Override
    public void setPotion(ItemStack stack, IPotion potion, int powerLevel, int timeLevel)
    {
        PotionUtil.writePotionNBT(NBTUtil.getNBTTag(stack), potion, powerLevel, timeLevel);
    }

    @Override
    public IPotion getPotion(ItemStack stack)
    {
        return PotionUtil.getPotionFromNBT(NBTUtil.getNBTTag(stack));
    }

    @Override
    public int getPowerLevel(ItemStack stack)
    {
        return PotionUtil.getPowerLevelFromNBT(NBTUtil.getNBTTag(stack));
    }

    @Override
    public int getTimeLevel(ItemStack stack)
    {
        return PotionUtil.getTimeLevelFromNBT(NBTUtil.getNBTTag(stack));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (IPotion potion : PotionRegistry.INSTANCE.getPotions())
        {
            for (int powerLevel = 1; powerLevel <= potion.getMaxPower(); powerLevel++)
            {
                for (int timeLevel = 1; timeLevel <= potion.getMaxTime(); timeLevel++)
                {
                    ItemStack stack = new ItemStack(item);
                    PotionUtil.writePotionNBT(NBTUtil.getNBTTag(stack), potion, powerLevel, timeLevel);
                    list.add(stack);
                }
            }
        }
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int pass)
    {
        return pass == 1 ? 0xFFFFFF : getPotion(stack).getColor(stack);
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass)
    {
        return pass == 0 ? colorIcon : potionIcon;
    }

    @Override
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
    {
        return getIcon(stack, renderPass);
    }

    @Override
    public IIcon getIconFromDamage(int p_77617_1_)
    {
        return potionIcon;
    }

    @Override
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }

    @Override
    public int getRenderPasses(int metadata)
    {
        return 2;
    }

    @Override
    public boolean hasEffect(ItemStack par1ItemStack)
    {
        return false;
    }
    
    @Override
    public boolean hasEffect(ItemStack par1ItemStack, int pass)
    {
        return pass == 0;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        return getPotion(stack).getLocalizedName(PotionUtil.getDataFromNBT(NBTUtil.getNBTTag(stack)));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        player.setItemInUse(stack, getMaxItemUseDuration(stack));
        return super.onItemRightClick(stack, world, player);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack p_77661_1_)
    {
        return EnumAction.drink;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack p_77626_1_)
    {
        return 32;
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player)
    {
        PotionUtil.applyPotion(getPotion(stack), stack, player);
        stack.stackSize--;
        return stack.stackSize <= 0 ? null : stack;
    }

    // Let's get rid of that pesky ItemPotion stuff

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_)
    {
        list.add("Power lvl " + getPowerLevel(stack));
        list.add("Time lvl " + getTimeLevel(stack) + " (" + StringUtils.ticksToElapsedTime(getPotion(stack).getTimeForLevel(getPowerLevel(stack), getTimeLevel(stack))) + ")");
    }

//    @SuppressWarnings("rawtypes")
//    @Override
//    public List getEffects(int p_77834_1_)
//    {
//        return null;
//    }
//
//    @SuppressWarnings("rawtypes")
//    @Override
//    public List getEffects(ItemStack p_77832_1_)
//    {
//        return null;
//    }
}
