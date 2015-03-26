package tterrag.potionapi.client;

import tterrag.potionapi.common.ContainerBetterBrewing;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntityBrewingStand;

public class GuiBetterBrewing extends GuiBrewingStand
{
    public GuiBetterBrewing(InventoryPlayer inv, TileEntityBrewingStand te)
    {
        super(inv, te);
        this.inventorySlots = new ContainerBetterBrewing(inv, te);
    }
}
