package lukeperkin.craftingtableii;

import java.util.ArrayList;

import net.minecraft.src.CraftingManager;
import net.minecraft.src.IRecipe;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemBucket;
import net.minecraft.src.ItemStack;

public class Zeldo {
	public static int MaxLevel = 3;
	public static IRecipe getCraftingRecipe(ItemStack item)
	{
		return getCraftingRecipe(item, new ArrayList());
	}
	public static IRecipe getCraftingRecipe(ItemStack item, ArrayList BlackList)
	{
		for(int i = 0; i < CraftingManager.getInstance().getRecipeList().size(); i++) {
			ItemStack CurItem = ((IRecipe)CraftingManager.getInstance().getRecipeList().get(i)).getRecipeOutput();
			if (TestItems(CurItem,item))
			{
				//if (!BlackList.contains(CurItem.itemID + "@" + CurItem.getItemDamage()))
					//System.out.println(CurItem.getItemNameandInformation().get(0) + " @@ " + CurItem.stackSize);
					return (IRecipe)CraftingManager.getInstance().getRecipeList().get(i);
				//else
				//	System.out.println("Blacklist Block " + CurItem.itemID + "@" + CurItem.getItemDamage());
			}
		}
		return null;
	}
	
	public static boolean TestItems(ItemStack i1, ItemStack i2)
	{
		//System.out.println(i1.itemID + "@" + i1.getItemDamage() + " Test " + i2.itemID + "@" + i2.getItemDamage() + " - " + i1.getItemNameandInformation().get(0));
		return i1.itemID == i2.itemID && (i1.getItemDamage() == i2.getItemDamage() || i1.getItemDamage() == -1 || i2.getItemDamage() == -1);
	}
	
	public static Object[] canPlayerCraft(InventoryPlayer ThePlayer, IRecipe TheItem)
	{
		return canPlayerCraft(ThePlayer, TheItem, new ArrayList(), 0);
	}
	public static Object[] canPlayerCraft(InventoryPlayer ThePlayer, IRecipe TheItem, ArrayList Blacklist, int Level)
	{
		int SlotCount = 0;
		ItemStack[] recipeIngredients = ContainerClevercraft.getRecipeIngredients(TheItem);
		if (Level > MaxLevel)
			return new Object[] {false, ThePlayer, SlotCount};
		if(recipeIngredients == null)
			return new Object[] {false, ThePlayer, SlotCount}; //This item isnt craftable and they must have it or else its a no go
		boolean playerHasAllIngredients = true;
		for(int i1 = 0; i1 < recipeIngredients.length; i1++) {
			if (Level == 0)
				Blacklist = new ArrayList();
			if(recipeIngredients[i1] == null)
				continue;
			
			ItemStack itemstack = recipeIngredients[i1].copy();
			itemstack.stackSize = 1;
			if (itemstack.itemID == 17)
				itemstack.setItemDamage(-1);
			Blacklist.add(itemstack.itemID + "@" + itemstack.getItemDamage());
			int slotindex = getFirstInventoryPlayerSlotWithItemStack(ThePlayer, itemstack);
			//System.out.println("Check: " + itemstack.getItemNameandInformation().get(0));
			
			if(slotindex != -1) {
				//System.out.println(itemstack.getItemNameandInformation().get(0) + ": " + ThePlayer.getStackInSlot(slotindex).stackSize + " - " + itemstack.stackSize);
				ThePlayer.decrStackSize(slotindex, itemstack.stackSize);
				if (itemstack.getItem() instanceof ItemBucket)
				{
					ThePlayer.addItemStackToInventory(new ItemStack(Item.bucketEmpty));
					SlotCount += 1;
				}
			} else {
				Object[] CanCraft = canPlayerCraft(ThePlayer, getCraftingRecipe(itemstack, Blacklist), Blacklist, Level+1);
				ThePlayer = (InventoryPlayer) CanCraft[1];
				//System.out.println("Check2: " + itemstack.getItemNameandInformation().get(0) + " - " + (Boolean)CanCraft[0]);
				if (!(Boolean)CanCraft[0])
				{
					playerHasAllIngredients = false;
					break;
				}
				SlotCount += ((Integer) CanCraft[2]) + 1;
				
				slotindex = getFirstInventoryPlayerSlotWithItemStack(ThePlayer, itemstack);
				if(slotindex != -1) {
					//System.out.println(itemstack.getItemNameandInformation().get(0) + ": " + ThePlayer.getStackInSlot(slotindex).stackSize + " - " + itemstack.stackSize);
					ThePlayer.decrStackSize(slotindex, itemstack.stackSize);
					
				} else {
					System.out.println("CraftingTableIII: There was an error! Error code 231");
				}
			}
		}
		if (playerHasAllIngredients)
		{
			ThePlayer.addItemStackToInventory(TheItem.getRecipeOutput().copy());
			//System.out.println("AddItems: " + TheItem.getRecipeOutput().getItemNameandInformation().get(0) + " - " + TheItem.getRecipeOutput().stackSize);
		}
		return new Object [] {playerHasAllIngredients, ThePlayer, SlotCount};
	}
	
	public static int getFirstInventoryPlayerSlotWithItemStack(InventoryPlayer inventory, ItemStack itemstack)
	{
		for(int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack itemstack1 = inventory.getStackInSlot(i);
			if(itemstack1 != null && itemstack1.itemID == itemstack.itemID && (itemstack1.getItemDamage() == itemstack.getItemDamage() || itemstack.getItemDamage() == -1)) {
				return i;
			}
		}
		
		return -1;
	}
}
