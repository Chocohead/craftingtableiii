package lukeperkin.craftingtableii;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryBasic;
import net.minecraft.src.ItemStack;

public class ContainerNull extends Container {
	
	private static InventoryBasic inventory = new InventoryBasic("tmp", 8*5);

	
	public ContainerNull()
	{
	}
	

	
	
	static InventoryBasic getInventory()
	{
		return inventory;
	}
	
	
	public void updateVisibleSlots(float f)
	{
	}
	
	public ItemStack slotClick(int slotIndex, int mouseButton, boolean shiftIsDown, EntityPlayer entityplayer)
    {	
		return null;
    }
	
	
	
	private void onCraftMatrixChanged(ItemStack recipeOutputStack)
	{
	}
	
	
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

}
