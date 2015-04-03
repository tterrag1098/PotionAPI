package tterrag.potionapi.common.brewing;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import tterrag.potionapi.api.brewing.IPotion;
import tterrag.potionapi.api.brewing.IPotionRegistry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PotionRegistry implements IPotionRegistry
{
    public static IPotionRegistry INSTANCE = new PotionRegistry();

    private List<IPotion> potions = Lists.newArrayList();
    private Map<String, IPotion> idToPotion = Maps.newHashMap();
    private Map<IPotion, IIcon> potionToIcon = Maps.newHashMap();

    @Override
    public void registerPotion(IPotion potion)
    {
        potions.add(potion);
        idToPotion.put(potion.getIdentifier(), potion);
    }

    @Override
    public IPotion getPotionByID(String string)
    {
        return idToPotion.get(string);
    }

    @Override
    public Collection<IPotion> getPotions()
    {
        return ImmutableList.copyOf(potions);
    }

    @Override
    public IIcon getIconFor(IPotion potion)
    {
        return potionToIcon.get(potion);
    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event)
    {
        if (event.map.getTextureType() == 1)
        {
            for (IPotion potion : getPotions())
            {
                potionToIcon.put(potion, potion.getIcon(event.map));
            }
        }
    }

    private PotionRegistry()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
