package lukeperkin.craftingtableii;

import java.io.File;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NetServerHandler;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.World;

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
	public static boolean isMutiplayer() {
		return true;
	}
	public static void SendPacketTo(EntityPlayer thePlayer, Packet sendUpdatePacket) {
		if (sendUpdatePacket != null) {
			ModLoader.getMinecraftServerInstance().configManager.sendPacketToPlayer(thePlayer.username, sendUpdatePacket);
		}
	}
	public static void HandleUpdateItems() {

	}
	public static File getMcDir()
	  {
	    return new File(".");
	  }
}
