package lukeperkin.craftingtableii;

import java.io.File;
import java.util.Timer;

import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.mod_CraftingTableIII;
import net.minecraft.src.forge.MinecraftForgeClient;

public class Proxy {
	//Client Side
	public static void Init()
	{
		// Setup block render.
		RenderCraftingTableII render = new RenderCraftingTableII();
		ModLoader.registerTileEntity(lukeperkin.craftingtableii.TileEntityCraftingTableII.class, "craftingtableII", render);
	}
	public static void TextSetup(String path)
	{
		MinecraftForgeClient.preloadTexture(path);
	}
	public static void SendMsg(String Text)
	{
		ModLoader.getMinecraftInstance().ingameGUI.addChatMessage(Text);
	}
	public static Object getGui(EntityPlayer player, TileEntityCraftingTableII tile) {
		return new GuiClevercraft(player, tile);
	}
	public static boolean IsClient() {
		return true;
	}
	public static EntityPlayer getPlayer(NetworkManager network) {
		return null;
	}
	public static void SendPacket(Packet packet)
	{
		ModLoader.sendPacket(packet);
	}
	public static boolean isMutiplayer() {
		return ModLoader.getMinecraftInstance().theWorld.isRemote;
	}
	public static void SendPacketTo(EntityPlayer thePlayer, Packet sendUpdatePacket) {

	}
	public static void HandleUpdateItems() {
		if (ModLoader.getMinecraftInstance().currentScreen instanceof GuiClevercraft)
		{
			((GuiClevercraft)ModLoader.getMinecraftInstance().currentScreen).updateContainer();
		}
	}
	public static File getMcDir()
	{
		return Minecraft.getMinecraftDir();
	}
	public static boolean SendContainerUpdate(EntityPlayer player) { return false; }

}
