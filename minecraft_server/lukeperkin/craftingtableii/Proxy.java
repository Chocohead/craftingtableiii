package lukeperkin.craftingtableii;

import java.io.File;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICrafting;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NetServerHandler;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Slot;

public class Proxy {
	//Server Side
	public static void Init()
	{
		ModLoader.registerTileEntity(lukeperkin.craftingtableii.TileEntityCraftingTableII.class, "craftingtableII");
	}
	public static void TextSetup(String path)
	{

	}
	public static void SendMsg(String Text)
	{
		System.out.println(Text);
	}
	public static Object getGui(EntityPlayer player, TileEntityCraftingTableII tile) {
		return null;
	}
	public static boolean IsClient() {
		return false;

	}
	public static EntityPlayer getPlayer(NetworkManager network) {
		return ((NetServerHandler)network.getNetHandler()).getPlayerEntity();
	}
	public static void SendPacket(Packet packet)
	{
		ModLoader.getMinecraftServerInstance().configManager.sendPacketToAllPlayers(packet);
	}
	public static boolean SendContainerUpdate(EntityPlayer player)
	{
		return ((ContainerClevercraft)((EntityPlayerMP)player).craftingInventory).UpdateInventory();
	}
	public static boolean isMutiplayer() {
		return true;
	}
	public static void SendPacketTo(EntityPlayer thePlayer, Packet sendUpdatePacket) {
		if (sendUpdatePacket != null) {
			//ModLoader.getMinecraftServerInstance().configManager.sendPacketToPlayer(thePlayer.username, sendUpdatePacket);
			((EntityPlayerMP)thePlayer).playerNetServerHandler.sendPacket(sendUpdatePacket);
		}
	}
	public static void HandleUpdateItems() {

	}
	public static File getMcDir()
	  {
	    return new File(".");
	  }
}
