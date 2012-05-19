package lukeperkin.craftingtableii;

import java.util.ArrayList;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.IRecipe;
import net.minecraft.src.Slot;
import net.minecraft.src.mod_CraftingTableIII;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiClevercraft extends GuiContainer {
	
	private float field_35312_g;
    private boolean field_35313_h;
    private boolean field_35314_i;
    private boolean shouldShowDescriptions;

    
	public GuiClevercraft(EntityPlayer entityplayer, TileEntityCraftingTableII tile)
    {
        super( new ContainerClevercraft(entityplayer, tile) );
        field_35312_g = 0.0F;
        field_35313_h = false;
        allowUserInput = true;
        entityplayer.craftingInventory = inventorySlots;
        ySize = 234;
        
        ((ContainerClevercraft)inventorySlots).updateVisibleSlots(0.0F);
    }
	
	public void updateContainer()
	{
		//((ContainerClevercraft)inventorySlots).populateSlotsWithRecipes();
		((ContainerClevercraft)inventorySlots).StartTimer();
	}
	
	public void initGui()
    {
		super.initGui();
    	//controlList.clear();

    }
	
	@Override
	protected void handleMouseClick(Slot slot, int i, int j, boolean flag)
    {
		if (slot.slotNumber > 3)
			super.handleMouseClick(slot, i, j, flag);
        //inventorySlots.slotClick(i, j, flag, mc.thePlayer);
    }

	
	public void drawScreen(int i, int j, float f)
    {
        boolean flag = Mouse.isButtonDown(0);
        int k = guiLeft;
        int l = guiTop;
        int i1 = k + 155;
        int j1 = l + 17;
        int k1 = i1 + 14;
        int l1 = j1 + 88 + 2;
        
        if(!field_35314_i && flag && i >= i1 && j >= j1 && i < k1 && j < l1)
        {
            field_35313_h = true;
        }
        if(!flag)
        {
            field_35313_h = false;
        }
        field_35314_i = flag;
        if(field_35313_h)
        {
            field_35312_g = (float)(j - (j1 + 8)) / ((float)(l1 - j1) - 16F);
            if(field_35312_g < 0.0F)
            {
                field_35312_g = 0.0F;
            }
            if(field_35312_g > 1.0F)
            {
                field_35312_g = 1.0F;
            }
            ((ContainerClevercraft)inventorySlots).updateVisibleSlots(field_35312_g);
        }
        
        for (int a = 0; a < ((ContainerClevercraft)inventorySlots).inventory.getSizeInventory(); a++)
        {
        	if ((Slot)this.inventorySlots.inventorySlots.get(a) instanceof SlotClevercraft)
        	{
        		SlotClevercraft theSlot = (SlotClevercraft) this.inventorySlots.inventorySlots.get(a);

        		if (theSlot.myIndex > -1 && getIsMouseOverSlot(theSlot, i, j))
        		{
        			for (int b=0; b<9; b++)
        			{
        				((ContainerClevercraft)inventorySlots).recipeItems.setInventorySlotContents(b, null);
        			}
        			ArrayList<ItemDetail> theRecipe = Zeldo.ValidRecipes.get(theSlot.myIndex);
        			int Counter = 0;
        			for (int b=0; b<theRecipe.size(); b++)
        			{
        				if (mod_CraftingTableIII.RecipeType == 1)
        				{
	        				if (theRecipe.get(b) != null)
	        					((ContainerClevercraft)inventorySlots).recipeItems.setInventorySlotContents(b, theRecipe.get(b).toItemStack());
	        				else
	        					((ContainerClevercraft)inventorySlots).recipeItems.setInventorySlotContents(b, null);
        				} else if (mod_CraftingTableIII.RecipeType == 0)
        				{
	        				if (theRecipe.get(b) != null)
	        				{
	        					((ContainerClevercraft)inventorySlots).recipeItems.setInventorySlotContents(Counter, theRecipe.get(b).toItemStack());
	        					Counter++;
	        				}
        				}
        			}
        			break; // Exit the loop, no need to waste cycles checking the rest
        		}
        	}
           
        }
        
        super.drawScreen(i, j, f);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(2896 /*GL_LIGHTING*/);
    }
	
	private boolean getIsMouseOverSlot(Slot slot, int i, int j)
    {
        int k = guiLeft;
        int l = guiTop;
        i -= k;
        j -= l;
        return i >= slot.xDisplayPosition - 1 && i < slot.xDisplayPosition + 16 + 1 && j >= slot.yDisplayPosition - 1 && j < slot.yDisplayPosition + 16 + 1;
    }
	
	protected void drawGuiContainerForegroundLayer()
    {
        fontRenderer.drawString("Crafting Table III", 8, 6, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int k = mc.renderEngine.getTexture("/gui/crafttableii.png");
        mc.renderEngine.bindTexture(k);
        int l = guiLeft;
        int i1 = guiTop;
        //Background
        drawTexturedModalRect(l, i1, 0, 0, xSize, ySize);
        int j1 = l + 155;
        int k1 = i1 + 17;
        int l1 = k1 + 88 + 2;
        //Scrolly bar
        drawTexturedModalRect(l + 154, i1 + 17 + (int)((float)(l1 - k1 - 17) * field_35312_g), 0, 240, 16, 16);
    }
    
    public void handleMouseInput()
    {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();
        ContainerClevercraft container = (ContainerClevercraft)inventorySlots;
        if(i != 0)
        {
            int j = (container.craftableRecipes.getSize() / 8 - 4) + 1;
            if(i > 0)
            {
                i = 1;
            }
            if(i < 0)
            {
                i = -1;
            }
            field_35312_g -= (double)i / (double)j;
            if(field_35312_g < 0.0F)
            {
                field_35312_g = 0.0F;
            }
            if(field_35312_g > 1.0F)
            {
                field_35312_g = 1.0F;
            }
            container.updateVisibleSlots(field_35312_g);
        }
    }
    
    public void resetScroll()
	{
		field_35312_g = 0.0F;
		((ContainerClevercraft)inventorySlots).updateVisibleSlots(field_35312_g);
	}
}
