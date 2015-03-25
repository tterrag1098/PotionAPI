package tterrag.potionapi.common.util;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.util.RegistryNamespaced;
import net.minecraftforge.event.world.WorldEvent;
import tterrag.core.common.Handlers.Handler;
import tterrag.core.common.Handlers.Handler.HandlerType;

import com.google.common.base.Throwables;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.RegistryDelegate;
import cpw.mods.fml.relauncher.ReflectionHelper;

/**
 * Modified to fit the PotionAPI project
 * 
 * @author CoFH [<a href=https://github.com/CoFH/CoFHLib/blob/master/src/main/java/cofh/lib/util/RegistryUtils.java>source</a>] under the LGPLv3
 *         License.
 */
@Handler(HandlerType.FORGE)
public final class ReplaceUtil
{
    @SuppressWarnings("unchecked")
    private static class Repl
    {
        private static IdentityHashMap<RegistryNamespaced, Multimap<String, Object>> replacements;
        private static Class<RegistryDelegate<?>> DelegateClass;

        @SuppressWarnings("rawtypes")
        private static void overwrite_do(RegistryNamespaced registry, String name, Object object, Object oldThing)
        {
            int id = registry.getIDForObject(oldThing);
            BiMap map = ((BiMap) registry.registryObjects);
            registry.underlyingIntegerMap.func_148746_a(object, id);
            map.remove(name);
            map.forcePut(name, object);
        }

        private static void alterDelegateChain(RegistryNamespaced registry, String id, Object object)
        {
            Multimap<String, Object> map = replacements.get(registry);
            List<Object> c = (List<Object>) map.get(id);
            int i = 0, e = c.size() - 1;
            Object end = c.get(e);
            for (; i <= e; ++i)
            {
                Object t = c.get(i);
                Repl.alterDelegate(t, end);
            }
        }

        private static void alterDelegate(Object obj, Object repl)
        {
            if (obj instanceof Item)
            {
                RegistryDelegate<Item> delegate = ((Item) obj).delegate;
                ReflectionHelper.setPrivateValue(DelegateClass, delegate, repl, "referant");
            }
        }

        static
        {
            replacements = new IdentityHashMap<RegistryNamespaced, Multimap<String, Object>>(2);
            try
            {
                DelegateClass = (Class<RegistryDelegate<?>>) Class.forName("cpw.mods.fml.common.registry.RegistryDelegate$Delegate");
            }
            catch (Throwable e)
            {
                Throwables.propagate(e);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onWorldLoad(WorldEvent.Load event)
    {
        if (Repl.replacements.size() < 1)
        {
            return;
        }
        for (Map.Entry<RegistryNamespaced, Multimap<String, Object>> entry : Repl.replacements.entrySet())
        {
            RegistryNamespaced reg = entry.getKey();
            Multimap<String, Object> map = entry.getValue();
            Iterator<String> v = map.keySet().iterator();
            while (v.hasNext())
            {
                String id = v.next();
                List<Object> c = (List<Object>) map.get(id);
                int i = 0, e = c.size() - 1;
                Object end = c.get(e);
                if (reg.getIDForObject(c.get(0)) != reg.getIDForObject(end))
                {
                    for (; i <= e; ++i)
                    {
                        Object t = c.get(i);
                        Object oldThing = reg.getObject(id);
                        Repl.overwrite_do(reg, id, t, oldThing);
                        Repl.alterDelegate(oldThing, end);
                    }
                }
            }
        }
    }

    public static void overwriteEntry(RegistryNamespaced registry, String name, Object object)
    {
        Object oldThing = registry.getObject(name);
        Repl.overwrite_do(registry, name, object, oldThing);
        Multimap<String, Object> reg = Repl.replacements.get(registry);
        if (reg == null)
        {
            Repl.replacements.put(registry, reg = ArrayListMultimap.create());
        }
        if (!reg.containsKey(name))
        {
            reg.put(name, oldThing);
        }
        reg.put(name, object);
        Repl.alterDelegateChain(registry, name, object);
    }
}
