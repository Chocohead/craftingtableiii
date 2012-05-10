package lukeperkin.craftingtableii;

import java.util.ArrayList;

import net.minecraft.src.CraftingManager;
import net.minecraft.src.IInventory;
import net.minecraft.src.IRecipe;
import net.minecraft.src.InventoryCrafting;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemBucket;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.forge.ForgeHooks;

public class Zeldo {
	public static int MaxLevel = 10;
	public static ArrayList ValidRecipes;
	public static ArrayList<ItemDetail> ValidOutput; 
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
	
	public static Object[] canPlayerCraft(InventoryPlayer ThePlayer, ItemDetail TheItem, IInventory Internal)
	{
		return canPlayerCraft(ThePlayer, ((TileEntityCraftingTableII)Internal).getCopy(), TheItem, 0, false, null, null);
	}
	public static Object[] canPlayerCraft(InventoryPlayer ThePlayer, IInventory Internal, ItemDetail TheItem, int Level, boolean UpdateWorld, ItemDetail Item1, ItemDetail Item2)
	{
		//ArrayList<ItemDetail> recipeIngredients = ContainerClevercraft.getRecipeIngredients(TheItem);
		int SlotCount = 0;
		InventoryPlayer ThePlayerBefore = new InventoryPlayer(ThePlayer.player);
		ThePlayerBefore.copyInventory(ThePlayer);
		int recipeIndex = ContainerClevercraft.getRecipeIngredients(TheItem);
		if (recipeIndex == -1)
			return new Object[] {false, ThePlayer, SlotCount, Internal};
		ArrayList<ItemDetail> recipeIngredients = (ArrayList<ItemDetail>) Zeldo.ValidRecipes.get(recipeIndex);
		
		
		
		
		
		if (Level > MaxLevel)
			return new Object[] {false, ThePlayer, SlotCount, Internal};
		
		boolean playerHasAllItems = true;
		for (int i=0; i<recipeIngredients.size(); i++)
		{
			if (recipeIngredients.get(i) == null)
				continue;
			if (recipeIngredients.get(i).equalsForceIgnore(Item2))
				return new Object[] {false, ThePlayerBefore, SlotCount, Internal}; //Look into this effecting player in some recipes
			int SlotIndex = getFirstInventoryPlayerSlotWithItemStack(ThePlayer, Internal, recipeIngredients.get(i).toItemStack());
			System.out.println("Slot: " + SlotIndex);
			if (SlotIndex > -1)
			{
				DecItemStackPlayer(ThePlayer, Internal, SlotIndex, recipeIngredients.get(i).StackSize, UpdateWorld); //ThePlayer.decrStackSize(SlotIndex, recipeIngredients.get(i).StackSize);
			} else {
				Object[] Result = canPlayerCraft(ThePlayer, Internal, recipeIngredients.get(i), Level+1, UpdateWorld, recipeIngredients.get(i), TheItem);
				ThePlayer = (InventoryPlayer) Result[1];
				Internal = (IInventory) Result[3];
				if ((Boolean)Result[0] != true)
				{
					playerHasAllItems = false;
					break;
				}
				SlotIndex = getFirstInventoryPlayerSlotWithItemStack(ThePlayer, Internal, recipeIngredients.get(i).toItemStack());
				System.out.println("Slot2: " + SlotIndex);
				if(SlotIndex != -1) {
					DecItemStackPlayer(ThePlayer, Internal, SlotIndex, recipeIngredients.get(i).StackSize, UpdateWorld); //ThePlayer.decrStackSize(SlotIndex, recipeIngredients.get(i).StackSize);
				}
				
			}
		}
		
		if (playerHasAllItems)
		{
			TheItem = Zeldo.ValidOutput.get(recipeIndex); //Fixes damage values and set the proper item stack size
			if (AddItemStackPlayer(ThePlayer, Internal, TheItem.toItemStack(), UpdateWorld) == false) //ThePlayer.addItemStackToInventory(TheItem.toItemStack());
			{
				return new Object[] {false, ThePlayerBefore, SlotCount, Internal}; //Look into this effecting player in some recipes
			}
			if (UpdateWorld){
				InventoryCrafting TempMatrix =GenCraftingMatrix(ContainerClevercraft.getRecipeIngredientsOLD(TheItem.iRecipe)); 
				TheItem.toItemStack().onCrafting(ThePlayer.player.worldObj, ThePlayer.player, 1);
				ModLoader.takenFromCrafting(ThePlayer.player, TheItem.toItemStack(), TempMatrix);
				ForgeHooks.onTakenFromCrafting(ThePlayer.player, TheItem.toItemStack(), TempMatrix);
				HandleCraftingMaxtrix(TempMatrix, ThePlayer);
			}
		}
		return new Object[] {playerHasAllItems, ThePlayer, SlotCount, Internal};
	}
	public static boolean AddItemStackPlayer(InventoryPlayer a, IInventory Internal, ItemStack b, boolean Update)
	{
		if (((TileEntityCraftingTableII)Internal).addItemStackToInventory(b))
		{
			return true;
		} else {
			return a.addItemStackToInventory(b);
		}
		
	}
	public static void DecItemStackPlayer(InventoryPlayer a, IInventory Internal, int Slot, int Amount, boolean Update)
	{
		if (Slot < 18)
			Internal.decrStackSize(Slot, Amount);
		else
			a.decrStackSize(Slot-18, Amount);
	}
	public static void HandleCraftingMaxtrix(InventoryCrafting CraftingMatrix, InventoryPlayer thePlayer)
	{
		for (int i=0; i<CraftingMatrix.getSizeInventory(); i++)
		{
			ItemStack CurStack = CraftingMatrix.getStackInSlot(i);
			if (CurStack != null)
			{
				CraftingMatrix.decrStackSize(i, 1);
				
					if(CurStack.getItem().hasContainerItem())
					{
						ItemStack item2 = new ItemStack(CurStack.getItem().getContainerItem());
						CraftingMatrix.setInventorySlotContents(i, item2);
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
				Items[i].stackSize = 1;
				Temp.setInventorySlotContents(i, Items[i]);
				//System.out.println("Not");
			}else {
				//System.out.println("Null");
			}
		}
		return Temp;
	}
	
	public static int getFirstInventoryPlayerSlotWithItemStack(InventoryPlayer inventory, IInventory Internal, ItemStack itemstack)
	{
		for(int i = 0; i < Internal.getSizeInventory()-1; i++) {
			ItemStack itemstack1 = Internal.getStackInSlot(i);
			if(itemstack1 != null && itemstack1.itemID == itemstack.itemID) {
				if (itemstack1.getItemDamage() == itemstack.getItemDamage() || itemstack.getItemDamage() == -1 || itemstack1.getItem().getHasSubtypes() == false)
					return i;
			}
		}
		for(int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack itemstack1 = inventory.getStackInSlot(i);
			if(itemstack1 != null && itemstack1.itemID == itemstack.itemID) {
				System.out.println("Need: " + itemstack.getItemDamage() + " - Want: " + itemstack1.getItemDamage());
				if (itemstack1.getItemDamage() == itemstack.getItemDamage() || itemstack.getItemDamage() == -1)
					return i+18;
				if (itemstack1.getItem().getHasSubtypes() == false) //Damageable so ignore
					return i+18;
			}
		}
		//System.out.println("Need: " + itemstack.itemID + "@" + itemstack.getItemDamage());
		return -1;
	}
	public static void InitRecipes() {
		if (RecipesInit) return;
		if (Proxy.IsClient() == false)
			System.out.println("Server Recipe");
		ValidRecipes = new ArrayList();
		ValidOutput = new ArrayList();
		//Get a list of the recipes in my form
		for(int i = 0; i < CraftingManager.getInstance().getRecipeList().size(); i++) {
			ItemStack[] recipeIngredients = ContainerClevercraft.getRecipeIngredientsOLD((IRecipe)CraftingManager.getInstance().getRecipeList().get(i));
			if (recipeIngredients != null) {
				ArrayList Temp = new ArrayList();
				for (int a=0; a<recipeIngredients.length; a++)
				{
					if (recipeIngredients[a] == null)
						Temp.add(null);
					else
					{
						if (recipeIngredients[a].itemID == 17)
							recipeIngredients[a].setItemDamage(-1);
						Temp.add(new ItemDetail(recipeIngredients[a].itemID, recipeIngredients[a].getItemDamage(), 1, (IRecipe)CraftingManager.getInstance().getRecipeList().get(i)));
					}
				}
				ValidRecipes.add(Temp);
				ItemStack Temp2 = ((IRecipe)CraftingManager.getInstance().getRecipeList().get(i)).getRecipeOutput();
				ValidOutput.add(new ItemDetail(Temp2.itemID, Temp2.getItemDamage(), Temp2.stackSize, (IRecipe)CraftingManager.getInstance().getRecipeList().get(i), true));
			}
		}
		
	}
}
