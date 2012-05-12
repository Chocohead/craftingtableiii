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

public class ContainerClevercraft extends Container {
	
	private static InventoryBasic inventory = new InventoryBasic("tmp", 8*5);
	private static IRecipe[] favouriteRecipes = new IRecipe[8];
	
	public InventoryCrafting craftMatrix;
    public InventoryCraftingTableII craftableRecipes;
    private List recipeList;
    private World worldObj;
    private EntityPlayer thePlayer;
    private Timer timer;
    public int MaxLevel = 3; //4 Runs
    public TileEntityCraftingTableII theTile;
    public float ScrollValue = 0.0F;
	
	public ContainerClevercraft(EntityPlayer aPlayer, TileEntityCraftingTableII tile)
	{
		worldObj = tile.worldObj;
		theTile = tile;
		thePlayer = aPlayer;
		craftMatrix = new InventoryCrafting(this, 3, 3);
        craftableRecipes = new InventoryCraftingTableII(1000);
        recipeList = Collections.unmodifiableList( CraftingManager.getInstance().getRecipeList() );
		
		for(int l2 = 0; l2 < 5; l2++)
        {
            for(int j3 = 0; j3 < 8; j3++)
            {
            	addSlot(new SlotClevercraft(thePlayer, inventory, craftMatrix, j3 + l2 * 8, 8 + j3 * 18, 18 + l2 * 18));
            }
        }
		for(int a = 0; a < 2; a++)
        {
        	for(int i = 0; i < 9; i++)
            {
                addSlot(new Slot(theTile, i + (a*9), 8 + i * 18, 112 + (18*a)));
            }
        }

        for(int j = 0; j < 3; j++)
        {
            for(int i1 = 0; i1 < 9; i1++)
            {
                addSlot(new Slot(thePlayer.inventory, i1 + j * 9 + 9, 8 + i1 * 18, 152 + j * 18));
            }
        }
        
        for(int i3 = 0; i3 < 9; i3++)
        {
            addSlot(new Slot(thePlayer.inventory, i3, 8 + i3 * 18, 211));
        }
        
        
        	populateSlotsWithRecipes();
        
        
        if(Proxy.IsClient() && Proxy.isMutiplayer()) {
        	timer = new Timer();
        	timer.schedule(new RemindTask(), mod_CraftingTableIII.SyncWaitTime);
        }

	}
	
	class RemindTask extends TimerTask {
	    public void run() {
	    	slotClick(-999, 5, false, null); //Throw a bad click to force it to bring items up
	    }
	}
	
	
	static InventoryBasic getInventory()
	{
		return inventory;
	}
	public void populateSlotsWithRecipes()
	{
		Zeldo.InitRecipes();
		long StartTime = new Date().getTime();
		craftableRecipes.clearRecipes();
		recipeList = Collections.unmodifiableList(recipeList);
		InventoryPlayer Temp = new InventoryPlayer( thePlayer );

		for(int i = 0; i < Zeldo.ValidOutput.size(); i++) { // Zeldo.ValidOutput.size()
			Temp.copyInventory(thePlayer.inventory);
			//System.out.println("RecipeCheck: " + i + "/" + Zeldo.ValidOutput.size() + " - " + Zeldo.ValidOutput.get(i).ItemID + "@" + Zeldo.ValidOutput.get(i).ItemDamage);
			if ((Boolean)Zeldo.canPlayerCraft(Temp, (ItemDetail)Zeldo.ValidOutput.get(i), theTile, i)[0])
			{
				craftableRecipes.addRecipe(((ItemDetail)Zeldo.ValidOutput.get(i)).iRecipe, i);
			}
		}		
		
		if (!Proxy.IsClient())
		{
			//Proxy.SendPacketTo(thePlayer, mod_CraftingTableIII.getInstance().SendUpdatePacket());
		}
		if (mod_CraftingTableIII.ShowTimings)
			System.out.println("Calculation Time: " + (new Date().getTime() - StartTime));
	}
	
	
	
	
	
	
	// Check InventorPlayer contains the ItemStack.
	private int getFirstInventoryPlayerSlotWithItemStack(InventoryPlayer inventory, ItemStack itemstack)
	{
		for(int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack itemstack1 = inventory.getStackInSlot(i);
			if(itemstack1 != null
					&& itemstack1.itemID == itemstack.itemID 
					&& (itemstack1.getItemDamage() == itemstack.getItemDamage() || itemstack.getItemDamage() == -1)) {
				return i;
			}
		}
		
		return -1;
	}
	public static int getRecipeIngredients(ItemDetail theItem)
	{
		return getRecipeIngredients(theItem, 0);
	}
	// Get a list of ingredient required to craft the recipe item.
	public static int getRecipeIngredients(ItemDetail theItem, int offset)
	{
		if (Zeldo.ValidOutput.size() <= offset)
			return -1;
		for (int i=offset; i<Zeldo.ValidOutput.size(); i++)
			if (Zeldo.ValidOutput.get(i).equals(theItem))
			{
				//System.out.println("getRecipeF: " + i);
				return i;
			}
				
		//System.out.println("getRecipeF: null");
		return -1;
	}
	
	@SuppressWarnings("unchecked")
	public static ItemStack[] getRecipeIngredientsOLD(IRecipe irecipe)
	{
		try {
			if (irecipe == null)
				return null;
			if(irecipe instanceof ShapedRecipes) {
				return (ItemStack[])ModLoader.getPrivateValue(ShapedRecipes.class, (ShapedRecipes)irecipe, 2);
			} else if(irecipe instanceof ShapelessRecipes) {
				if (irecipe.getRecipeOutput().getItem().getItemName() != null)
				{
					if (irecipe.getRecipeOutput().getItem().getItemName().equalsIgnoreCase("tile.rpwire"))
					{
						return null;
					}
				}
				ArrayList recipeItems = new ArrayList((List)ModLoader.getPrivateValue(ShapelessRecipes.class, (ShapelessRecipes)irecipe, 1));
				return (ItemStack[])recipeItems.toArray(new ItemStack[recipeItems.size()]);
			} else {
				String className = irecipe.getClass().getName();
				if(className.equals("ic2.common.AdvRecipe")) {
					return (ItemStack[]) ModLoader.getPrivateValue((Class)irecipe.getClass(), (Object)irecipe, "input");
				} else if(className.equals("ic2.common.AdvShapelessRecipe")) {
					return (ItemStack[]) ModLoader.getPrivateValue((Class)irecipe.getClass(), (Object)irecipe, "input");
				} else {
					if (mod_CraftingTableIII.ShowTimings)
						System.out.println("Invalid Recipe Class: " + className);
					return null;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void updateVisibleSlots(float f)
	{
		ScrollValue = f;
		int numberOfRecipes = craftableRecipes.getSize();
		int i = (numberOfRecipes / 8 - 4) + 1;
        int j = (int)((double)(f * (float)i) + 0.5D);
        if(j < 0)
            j = 0;
        
        for(int k = 0; k < 5; k++) {
            for(int l = 0; l < 8; l++) {
                int i1 = l + (k + j) * 8;
                Slot slot = (Slot)inventorySlots.get(l + k * 8);
                if(i1 >= 0 && i1 < numberOfRecipes) {
                	ItemStack recipeOutput = craftableRecipes.getRecipeOutput(i1);
                	if(recipeOutput != null) {
                		inventory.setInventorySlotContents(l + k * 8, recipeOutput);
                    	if(slot instanceof SlotClevercraft) {
                    		((SlotClevercraft)slot).setIRecipe( craftableRecipes.getIRecipe(i1),  craftableRecipes.getListIndex(i1));
                    	}
                	} else {
                		inventory.setInventorySlotContents(l + k * 8, null);
                		if(slot instanceof SlotClevercraft) {
                    		((SlotClevercraft)slot).setIRecipe(null, -1);
                    	}
                	}
                } else {
                	inventory.setInventorySlotContents(l + k * 8, null);
                	if(slot instanceof SlotClevercraft) {
                		((SlotClevercraft)slot).setIRecipe(null, -1);
                	}
                }
            }
        }
	}
	
	public ItemStack slotClick(int slotIndex, int mouseButton, boolean shiftIsDown, EntityPlayer entityplayer)
    {
		if(slotIndex != -999 
				&& inventorySlots.size() > slotIndex
				&& slotIndex >= 0
				&& inventorySlots.get(slotIndex) != null 
				&& inventorySlots.get(slotIndex) instanceof SlotClevercraft) {
			
			// Check if the currently held itemstack is different to the clicked itemstack.
			ItemStack itemstack = inventory.getStackInSlot(slotIndex);
			ItemStack playerItemStack = entityplayer.inventory.getItemStack();
			boolean currentItemStackIsDifferent = false;
			if(playerItemStack != null && itemstack != null) {
				if(playerItemStack.itemID == itemstack.itemID 
						&& (itemstack.getItemDamage() == -1 || itemstack.getItemDamage() == playerItemStack.getItemDamage())) {
					currentItemStackIsDifferent = false;
				} else {
					currentItemStackIsDifferent = true;
				}
			}
			
			if(currentItemStackIsDifferent)
				return null;
			
			// Ignore right click.
			if(mouseButton == 1) {
				return null;
			} else if(shiftIsDown) {
				onRequestMaximumRecipeOutput( (SlotClevercraft)inventorySlots.get(slotIndex) );
				populateSlotsWithRecipes();
				updateVisibleSlots(ScrollValue);
				return null;
			} else {
				if( !onRequestSingleRecipeOutput( (SlotClevercraft)inventorySlots.get(slotIndex) ) )
					populateSlotsWithRecipes();
					updateVisibleSlots(ScrollValue);
					return null;
			}
		}
		
		if(shiftIsDown) {
			populateSlotsWithRecipes();
			updateVisibleSlots(ScrollValue);
			return null;
		} else {
			ItemStack itemstack = super.slotClick(slotIndex, mouseButton, shiftIsDown, entityplayer);
			populateSlotsWithRecipes();
			updateVisibleSlots(ScrollValue);
			return itemstack;
		}
    }
	public boolean onRequestSingleRecipeOutput( SlotClevercraft slot )
	{
		IRecipe irecipe = slot.getIRecipe();
		if(irecipe == null)
			return false;
		
		return onRequestSingleRecipeOutput(thePlayer, irecipe, theTile, slot.myIndex);
	}
	
	public static boolean onRequestSingleRecipeOutput(EntityPlayer thePlayer, IRecipe irecipe, TileEntityCraftingTableII Internal, int RecipeIndex)
	{
		
		ItemStack recipeOutputStack = irecipe.getRecipeOutput().copy();
		
		if (Proxy.IsClient() && Proxy.isMutiplayer())
		{
			mod_CraftingTableIII.getInstance().SendCraftingPacket(irecipe.getRecipeOutput().copy(), false, Internal.xCoord, Internal.yCoord, Internal.zCoord, RecipeIndex);
		}
		
		InventoryPlayer Temp = new InventoryPlayer( thePlayer );
		Temp.copyInventory(thePlayer.inventory);
		
		InventoryPlayer inventoryPlayer = thePlayer.inventory;
		Object[] iTemp = Zeldo.canPlayerCraft(inventoryPlayer, Internal, new ItemDetail(irecipe.getRecipeOutput()), 0, true, null, null, RecipeIndex);
		Internal.theInventory = ((TileEntityCraftingTableII)iTemp[3]).theInventory;
		thePlayer.inventory.copyInventory((InventoryPlayer) iTemp[1]) ;
		
		//onCraftMatrixChanged(recipeOutputStack);
		return false;
	}
	private void onRequestMaximumRecipeOutput( SlotClevercraft slot )
	{
		IRecipe irecipe = slot.getIRecipe();
		if(irecipe == null)
			return;
		
		onRequestMaximumRecipeOutput(thePlayer, irecipe, theTile, slot.myIndex);
	}
	public static void onRequestMaximumRecipeOutput(EntityPlayer thePlayer, IRecipe irecipe, TileEntityCraftingTableII Internal, int RecipeIndex)
	{	
		
		
		ItemStack recipeOutputStack = irecipe.getRecipeOutput().copy();
		//this.addFavouriteRecipe(irecipe);
		
		if (Proxy.IsClient() && Proxy.isMutiplayer())
		{
			mod_CraftingTableIII.getInstance().SendCraftingPacket(irecipe.getRecipeOutput().copy(), true, Internal.xCoord, Internal.yCoord, Internal.zCoord, RecipeIndex);
		}
		
		InventoryPlayer Temp = new InventoryPlayer( thePlayer );
		Temp.copyInventory(thePlayer.inventory);
		
		InventoryPlayer inventoryPlayer = thePlayer.inventory;
		int GoTo = 64;
		if (irecipe.getRecipeOutput().getMaxStackSize() > 1) {
			GoTo = irecipe.getRecipeOutput().getMaxStackSize() / irecipe.getRecipeOutput().stackSize ;
		}
		for (int i=0; i<GoTo; i++)
		{
				Temp.copyInventory(thePlayer.inventory);
				if ((Boolean)Zeldo.canPlayerCraft(Temp, new ItemDetail(irecipe.getRecipeOutput()), Internal, RecipeIndex)[0])
				{
					Object[] iTemp = Zeldo.canPlayerCraft(inventoryPlayer, Internal, new ItemDetail(irecipe.getRecipeOutput()), 0, true, null, null, RecipeIndex);
					Internal.theInventory = ((TileEntityCraftingTableII)iTemp[3]).theInventory;
					thePlayer.inventory.copyInventory((InventoryPlayer) iTemp[1]) ;
				} else {
					break;
				}
		}
		
		//onCraftMatrixChanged(recipeOutputStack);
	}
	
	private void onCraftMatrixChanged(ItemStack recipeOutputStack)
	{
		InventoryPlayer inventoryPlayer = thePlayer.inventory;
		// Call custom hooks.
		ModLoader.takenFromCrafting(thePlayer, recipeOutputStack, craftMatrix);
		ForgeHooks.onTakenFromCrafting(thePlayer, recipeOutputStack, craftMatrix);
		// Remove items from the craftMatrix and replace container items.
		for(int i = 0; i < craftMatrix.getSizeInventory(); i++)
		{
            ItemStack itemstack1 = craftMatrix.getStackInSlot(i);
            if(itemstack1 != null)
            {
                craftMatrix.decrStackSize(i, 1);
                if(itemstack1.getItem().hasContainerItem())
                {
                    craftMatrix.setInventorySlotContents(i, new ItemStack(itemstack1.getItem().getContainerItem()));
                }
            }
        }
        // Transfer any remaining items in the craft matrix to the player.
        for(int i = 0; i < craftMatrix.getSizeInventory(); i++) {
        	ItemStack itemstack = craftMatrix.getStackInSlot(i);
        	if(itemstack != null) {
        		inventoryPlayer.addItemStackToInventory(itemstack);
        		craftMatrix.setInventorySlotContents(i, null);
        	}
        }
	}
	
	private static void addFavouriteRecipe(IRecipe recipe)
	{
		for(int i = 7; i > 0; i--) {
			favouriteRecipes[i] = favouriteRecipes[i-1];
			if(favouriteRecipes[i] == recipe)
				favouriteRecipes[i] = null;
		}
		favouriteRecipes[0] = recipe;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}
	public boolean isUsableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

}
