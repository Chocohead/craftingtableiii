package lukeperkin.craftingtableii;

public class ItemDetail {
	public int ItemID = 0;
	public int ItemDamage = 0;
	public int StackSize = 0;
	
	public ItemDetail(int ID, int Damage, int Size)
	{
		this.ItemID = ID;
		this.ItemDamage = Damage;
		this.StackSize = Size;
	}
	public ItemDetail(int ID, int Damage)
	{
		this(ID, Damage, 1);
	}
	public ItemDetail(int ID)
	{
		this(ID, 0, 1);
	}
	@Override 
	public boolean equals(Object aThat) {
		System.out.println("Equals");
		if (aThat == this) return true;
		if (!(aThat instanceof ItemDetail)) return false;
		
		ItemDetail Temp = (ItemDetail)aThat;
		
		return Temp.ItemID == this.ItemID && Temp.ItemDamage == this.ItemDamage && Temp.StackSize == this.StackSize;
	}
}
