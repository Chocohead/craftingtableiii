package lukeperkin.craftingtableii;

import net.minecraft.src.IRecipe;
import net.minecraft.src.ItemStack;

public class ItemDetail {
	public int ItemID = 0;
	public int ItemDamage = 0;
	public int StackSize = 0;
	public boolean IgnoreStackSize = false; //Ignores the stack size when doing equals (good for recipe finding)
	public IRecipe iRecipe;
	
	public ItemDetail(int ID, int Damage, int Size, IRecipe theRecipe, boolean IgnoreStack)
	{
		this.ItemID = ID;
		this.ItemDamage = Damage;
		this.StackSize = Size;
		this.IgnoreStackSize = IgnoreStack;
		iRecipe = theRecipe;
	}
	public ItemDetail(int ID, int Damage, int Size, IRecipe theRecipe)
	{
		this(ID, Damage, Size, theRecipe, false);
	}
	public ItemDetail(int ID, int Damage)
	{
		this(ID, Damage, 1, null);
	}
	public ItemDetail(int ID)
	{
		this(ID, 0, 1, null);
	}
	public ItemDetail(ItemStack aItem)
	{
		this(aItem.itemID, aItem.getItemDamage(), aItem.stackSize, null);
	}
	@Override 
	public boolean equals(Object aThat) {
		//System.out.println("Equals");
		if (aThat == this) return true;
		if (!(aThat instanceof ItemDetail)) return false;
		
		ItemDetail Temp = (ItemDetail)aThat;
		//System.out.println(Temp.ItemID + ":" + this.ItemID + " - " + Temp.ItemDamage + ":" + this.ItemDamage + " - " + Temp.StackSize + ":" + this.StackSize);
		return Temp.ItemID == this.ItemID && (Temp.ItemDamage == this.ItemDamage || this.ItemDamage == -1 || Temp.ItemDamage == -1) && (Temp.StackSize == this.StackSize || IgnoreStackSize);
	}
	public boolean equalsForceIgnore(Object aThat) {
		//System.out.println("Equals");
		if (aThat == this) return true;
		if (!(aThat instanceof ItemDetail)) return false;
		
		ItemDetail Temp = (ItemDetail)aThat;
		//System.out.println(Temp.ItemID + ":" + this.ItemID + " - " + Temp.ItemDamage + ":" + this.ItemDamage + " - " + Temp.StackSize + ":" + this.StackSize);
		return Temp.ItemID == this.ItemID && (Temp.ItemDamage == this.ItemDamage || this.ItemDamage == -1 || Temp.ItemDamage == -1);
	}
	public ItemStack toItemStack()
	{
		return new ItemStack(ItemID, StackSize, ItemDamage);
	}
}
