package lukeperkin.craftingtableii;

import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

public class SlotIntercept extends Slot {
	ContainerClevercraft theCont;
	public SlotIntercept(IInventory par1iInventory, int par2, int par3, int par4, ContainerClevercraft cont) {
		super(par1iInventory, par2, par3, par4);
		theCont = cont;
	}
	@Override
	 public boolean isItemValid(ItemStack par1ItemStack)
	    {
	        return false;
	    }

}
