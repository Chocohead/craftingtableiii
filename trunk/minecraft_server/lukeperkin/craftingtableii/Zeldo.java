package lukeperkin.craftingtableii;

import java.util.ArrayList;

import net.minecraft.src.Container;
import net.minecraft.src.CraftingManager;
import net.minecraft.src.IRecipe;
import net.minecraft.src.InventoryCrafting;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemBucket;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.forge.ForgeHooks;

public class Zeldo {
	public static int MaxLevel = 3;
	public static ArrayList ValidRecipes;
	public static ArrayList ValidOutput; 
	public static boolean RecipesInit = false;
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
		return canPlayerCraft(ThePlayer, TheItem, new ArrayList(), 0, false);
	}
	public static Object[] canPlayerCraft(InventoryPlayer ThePlayer, IRecipe TheItem, ArrayList Blacklist, int Level, boolean UpdateWorld)
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
				Object[] CanCraft = canPlayerCraft(ThePlayer, getCraftingRecipe(itemstack, Blacklist), Blacklist, Level+1, UpdateWorld);
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
					//System.out.println("CraftingTableIII: There was an error! Error code 231");
					//return new Object[] {false, ThePlayer, SlotCount};
				}
			}
		}
		if (playerHasAllIngredients)
		{
			ThePlayer.addItemStackToInventory(TheItem.getRecipeOutput().copy());
			if (UpdateWorld){
				InventoryCrafting TempMatrix =GenCraftingMatrix(recipeIngredients); 
				TheItem.getRecipeOutput().onCrafting(ThePlayer.player.worldObj, ThePlayer.player, 1);
				ModLoader.takenFromCrafting(ThePlayer.player, TheItem.getRecipeOutput().copy(), TempMatrix);
				ForgeHooks.onTakenFromCrafting(ThePlayer.player, TheItem.getRecipeOutput().copy(), TempMatrix);
				HandleCraftingMaxtrix(TempMatrix, ThePlayer);
			}
			
			//System.out.println("AddItems: " + TheItem.getRecipeOutput().getItemNameandInformation().get(0) + " - " + TheItem.getRecipeOutput().stackSize);
		}
		return new Object [] {playerHasAllIngredients, ThePlayer, SlotCount};
	}
	public static void HandleCraftingMaxtrix(InventoryCrafting CraftingMatrix, InventoryPlayer thePlayer)
	{
		for (int i=0; i<CraftingMatrix.getSizeInventory(); i++)
		{
			if (CraftingMatrix.getStackInSlot(i) != null)
			{
				CraftingMatrix.decrStackSize(i, 1);
				if (CraftingMatrix.getStackInSlot(i) != null)
				{
					if(CraftingMatrix.getStackInSlot(i).getItem().hasContainerItem())
					{
						ItemStack item2 = new ItemStack(CraftingMatrix.getStackInSlot(i).getItem().getContainerItem());
						item2.setItemDamage(CraftingMatrix.getStackInSlot(i).getItemDamage());
						CraftingMatrix.setInventorySlotContents(i, item2);
					}
					
				}

			}
		}
		for(int i = 0; i <CraftingMatrix.getSizeInventory(); i++) {
			ItemStack itemstack = CraftingMatrix.getStackInSlot(i);
			if(itemstack != null) {
				thePlayer.addItemStackToInventory(itemstack.copy());
				CraftingMatrix.setInventorySlotContents(i, null);
			}
		}
	}
	public static InventoryCrafting GenCraftingMatrix(ItemStack[] Items)
	{
		InventoryCrafting Temp = new InventoryCrafting(new ContainerNull(), 3, 3);
		for (int i=0; i<Items.length; i++)
		{
			if (Items[i] != null) {
				Temp.setInventorySlotContents(i, Items[i]);
				//System.out.println("Not");
			}else {
				//System.out.println("Null");
			}
		}
		return Temp;
	}
	
	public static int getFirstInventoryPlayerSlotWithItemStack(InventoryPlayer inventory, ItemStack itemstack)
	{
		for(int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack itemstack1 = inventory.getStackInSlot(i);
			if(itemstack1 != null && itemstack1.itemID == itemstack.itemID) {
				if (itemstack1.getItemDamage() == itemstack.getItemDamage() || itemstack.getItemDamage() == -1)
					return i;
				if (itemstack1.getItem().getHasSubtypes() == false) //Damageable so ignore
					return i;
			}
		}
		
		return -1;
	}
	public static void InitRecipes() {
		if (RecipesInit) return;
		ValidRecipes = new ArrayList();
		//Get a list of the recipes in my form
		for(int i = 0; i < CraftingManager.getInstance().getRecipeList().size(); i++) {
			ItemStack[] recipeIngredients = ContainerClevercraft.getRecipeIngredients((IRecipe)CraftingManager.getInstance().getRecipeList().get(i));
			ArrayList Temp = new ArrayList();
			for (int a=0; a<recipeIngredients.length; a++)
			{
				Temp.add(new ItemDetail(recipeIngredients[a].itemID, recipeIngredients[a].getItemDamage(), recipeIngredients[a].stackSize));
			}
			ValidRecipes.add(Temp);
			ItemStack Temp2 = ((IRecipe)CraftingManager.getInstance().getRecipeList().get(i)).getRecipeOutput();
			ValidOutput.add(new ItemDetail(Temp2.itemID, Temp2.getItemDamage(), Temp2.stackSize));
		}
		
	}
}
