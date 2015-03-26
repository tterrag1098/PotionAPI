package tterrag.potionapi;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemReed;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import tterrag.core.IModTT;
import tterrag.core.common.Handlers.Handler;
import tterrag.core.common.Handlers.Handler.HandlerType;
import tterrag.core.common.Handlers.Handler.Inst;
import tterrag.potionapi.client.GuiBetterBrewing;
import tterrag.potionapi.common.BlockBetterBrewing;
import tterrag.potionapi.common.ContainerBetterBrewing;
import tterrag.potionapi.common.ItemBetterPotion;
import tterrag.potionapi.common.TileBetterBrewing;
import tterrag.potionapi.common.brewing.PotionRegistry;
import tterrag.potionapi.common.effect.EffectData;
import tterrag.potionapi.common.potions.PotionTest;
import tterrag.potionapi.common.util.ReplaceUtil;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

import static tterrag.potionapi.PotionAPI.*;

@Mod(modid = MODID, name = NAME, version = VERSION, dependencies = "after:ttCore")
@Handler(value = HandlerType.FORGE, getInstFrom = Inst.FIELD)
public class PotionAPI implements IModTT, IGuiHandler
{
    public static final String MODID = "potionapi";
    public static final String NAME = "PotionAPI";
    public static final String VERSION = "@VERSION@";

    public static Item potion;
    public static Block brewingStand;

    @Instance
    public static PotionAPI INSTANCE;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, INSTANCE);

        potion = new ItemBetterPotion();
        GameRegistry.registerItem(potion, "potion");

        brewingStand = new BlockBetterBrewing();
        Blocks.brewing_stand = brewingStand;
        ((ItemReed) Items.brewing_stand).field_150935_a = brewingStand;
        ReplaceUtil.overwriteEntry(Block.blockRegistry, "minecraft:brewing_stand", brewingStand);

        GameRegistry.registerTileEntity(TileBetterBrewing.class, "tileBetterBrewing");

        PotionRegistry.INSTANCE.registerPotion(new PotionTest());
    }

    @SubscribeEvent
    public void onEntityConstructing(EntityConstructing event)
    {
        if (event.entity instanceof EntityLivingBase)
        {
            event.entity.registerExtendedProperties(EffectData.IDENT, new EffectData());
        }
    }

    @Override
    public String modid()
    {
        return MODID;
    }

    @Override
    public String name()
    {
        return NAME;
    }

    @Override
    public String version()
    {
        return VERSION;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileBetterBrewing)
        {
            return new ContainerBetterBrewing(player.inventory, (TileEntityBrewingStand) world.getTileEntity(x, y, z));
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileBetterBrewing)
        {
            return new GuiBetterBrewing(player.inventory, (TileEntityBrewingStand) world.getTileEntity(x, y, z));
        }
        return null;
    }
}
