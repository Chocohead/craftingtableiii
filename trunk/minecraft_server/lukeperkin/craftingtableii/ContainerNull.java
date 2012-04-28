package lukeperkin.craftingtableii;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.src.Container;
import net.minecraft.src.CraftingManager;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IRecipe;
import net.minecraft.src.InventoryBasic;
import net.minecraft.src.InventoryCrafting;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.ShapedRecipes;
import net.minecraft.src.ShapelessRecipes;
import net.minecraft.src.Slot;
import net.minecraft.src.World;
import net.minecraft.src.mod_CraftingTableIII;
import net.minecraft.src.forge.ForgeHooks;

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
