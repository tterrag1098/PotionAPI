package tterrag.potionapi.common.brewing;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import tterrag.potionapi.api.brewing.IPotion;
import tterrag.potionapi.api.brewing.IPotionRegistry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PotionRegistry implements IPotionRegistry
{
    public static IPotionRegistry INSTANCE = new PotionRegistry();

    private List<IPotion> potions = Lists.newArrayList();
    private Map<String, IPotion> idToPotion = Maps.newHashMap();

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
}
