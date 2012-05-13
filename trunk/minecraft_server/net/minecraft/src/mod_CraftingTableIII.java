package net.minecraft.src;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;

import lukeperkin.craftingtableii.BlockClevercraft;
import lukeperkin.craftingtableii.ContainerClevercraft;
import lukeperkin.craftingtableii.Proxy;
import lukeperkin.craftingtableii.TileEntityCraftingTableII;
import lukeperkin.craftingtableii.Zeldo;
import net.minecraft.src.forge.Configuration;
import net.minecraft.src.forge.IConnectionHandler;
import net.minecraft.src.forge.IGuiHandler;
import net.minecraft.src.forge.IPacketHandler;
import net.minecraft.src.forge.MessageManager;
import net.minecraft.src.forge.MinecraftForge;
import net.minecraft.src.forge.NetworkMod;
import net.minecraft.src.forge.Property;

public class mod_CraftingTableIII extends NetworkMod implements IGuiHandler, IConnectionHandler, IPacketHandler {
	
	@MLProp public static int blockIDCraftingTableIII = 126; 
	@MLProp public static int guiIDCraftingTableIII = 235;
	
	public static Block blockCraftingTableIII;
	public static IRecipe[] lastRecipesCrafted;
	public static int numberOfLastRecipesCrafted;
	public static int craftingTableModelID;
	public static int SyncWaitTime = 2; //MS to wait before force syncing
	public static boolean ShowTimings = false;
	public static boolean CheckForUpdates = true;
	
	public static String UpdateURL = "http://dl.dropbox.com/u/73217561/craftingtableIII.txt";
	public static String Version = "Beta1.6";
	public static String NewVersion = null;
	
	private static mod_CraftingTableIII clevercraftInstance;
	private static ContainerClevercraft containerClevercraft;
	
	public static final int kPacketTypeSingleCraftingRequest = 0;
	public static final int kPacketTypeMaximumCraftingRequest = 1;
	public static final int kPacketTypeUpdateItems = 2;
	
	public static String ChannelName = "CTIII";
	
	public static Configuration config;
	
	public static String texturePath = "/blockimage/crafttableii_terrain.png";
	
	public static boolean EnableSound = true;
	public static boolean EnableDoor = true;
	
	
	public mod_CraftingTableIII() {
		
		if (CheckForUpdates)
		{
			CheckForUpdates();
	        if (!Version.equalsIgnoreCase(NewVersion))
	        	System.out.println("[CraftingTableIII]Theres a new version out! (" + NewVersion + ")");
			else
				System.out.println("[CraftingTableIII] Up to date!");
		}
		
        
        ModLoader.setInGameHook(this, true, true);
		Proxy.TextSetup(texturePath);
		
		config = new Configuration(new File(new File(Proxy.getMcDir(), "/config/"), "CraftingTableIII.cfg"));
	    config.load();
	    this.blockIDCraftingTableIII = Integer.parseInt(config.getOrCreateIntProperty("blockIDCraftingTableIII", "block", blockIDCraftingTableIII).value);
	    this.EnableSound = Boolean.parseBoolean(config.getOrCreateBooleanProperty("enableSound", "general", true).value);
	    this.EnableDoor = Boolean.parseBoolean(config.getOrCreateBooleanProperty("enableDoor", "general", true).value);
	    
	    Property temp = config.getOrCreateBooleanProperty("ShowTimings", "general", this.ShowTimings);
	    temp.comment = "Set to true if you are lagging and Zeldo wants your Timings";
	    this.ShowTimings = Boolean.parseBoolean(temp.value);
	    
	    temp = config.getOrCreateBooleanProperty("CheckForUpdates", "general", this.CheckForUpdates);
	    temp.comment = "Keep on true unless you don't want the newest versions";
	    this.CheckForUpdates = Boolean.parseBoolean(temp.value);
	    
	    temp = config.getOrCreateIntProperty("SyncWaitTime", "general", this.SyncWaitTime);
	    temp.comment = "Usually can be left at 2. Only raise if you have to click after opening CT3 on a server. RAISE ONLY BY 1 EVERY TIME";
	    this.SyncWaitTime = Integer.parseInt(temp.value);
	    
	    config.save();
	    
	    
		clevercraftInstance = this;
		
		craftingTableModelID = ModLoader.getUniqueBlockModelID(this, true);
		lastRecipesCrafted = new IRecipe[8];
		numberOfLastRecipesCrafted = 0;
		
		initBlocks();
		
		ModLoader.registerBlock(blockCraftingTableIII);
		ModLoader.addName(blockCraftingTableIII, "Crafting Table III");
		ModLoader.addShapelessRecipe(new ItemStack(blockCraftingTableIII, 1), new Object[]{
			Block.workbench, Item.book
		});
		//ModLoader.addShapelessRecipe(new ItemStack(blockCraftingTableIII, 1), new Object[]{
		//	Block.dirt
		//});
		
		Proxy.Init();

		MinecraftForge.setGuiHandler(this, this);
		MinecraftForge.registerConnectionHandler(this);
		
		//List<ItemDetail> Temp = new ArrayList<ItemDetail>();
		//Temp.add(new ItemDetail(100, 45, 1));
		//System.out.println("Test: " + (Temp.contains(new ItemDetail(100, 45, 1))));
		
	}
	
	
	
	public static void CheckForUpdates() {
		URL oracle;
		try {
			oracle = new URL(UpdateURL);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(oracle.openStream()));

			String inputLine;
			inputLine = in.readLine();
			in.close();
			NewVersion = inputLine;
		} catch (Exception e) {
			System.out.println("There was an error checking for updates...");
		}
        
	}

	public static mod_CraftingTableIII getInstance()
	{
		return clevercraftInstance;
	}
	
	 @Override
     public boolean clientSideRequired()
     {
             return true;
     }

     @Override
     public boolean serverSideRequired()
     {
             return false;
     }
	
	
	public static void initBlocks()
	{
		blockCraftingTableIII = new BlockClevercraft(blockIDCraftingTableIII);
	}
	
	public static void addLastRecipeCrafted(IRecipe recipe) {
		//Check if recipe is already in list.
		for(int i = 0; i < 8; i++) {
			IRecipe recipe1 = lastRecipesCrafted[i];
			if(recipe1 != null && recipe1.equals(recipe)){
				return;
			}
		}
		
		for(int i = 6; i >= 0; i--) {
			IRecipe recipe1 = lastRecipesCrafted[i];
			if(recipe1 != null) {
				lastRecipesCrafted[i+1] = recipe1;
			}
		}
		
		lastRecipesCrafted[0] = recipe;
		numberOfLastRecipesCrafted++;
		if(numberOfLastRecipesCrafted > 8)
			numberOfLastRecipesCrafted = 8;
	}

	@Override
	public String getVersion() {
		return "(" + Version + ", MC1.2.5)";
	}


	@Override
	public Object getGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == guiIDCraftingTableIII)
		{
			if (Proxy.IsClient())
				return Proxy.getGui(player, (TileEntityCraftingTableII)world.getBlockTileEntity(x, y, z));
			else
				return new ContainerClevercraft(player, (TileEntityCraftingTableII)world.getBlockTileEntity(x, y, z));
		}
		return null;
	}

	@Override
	public void load() {
		
	}

	@Override
	public void onPacketData(NetworkManager network, String channel, byte[] data) {
		DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(data));
		try {
			int PacketID = dataStream.readInt();
			//System.out.println("PacketID: " + PacketID);
			Zeldo.InitRecipes();
			if (PacketID == kPacketTypeSingleCraftingRequest && !Proxy.IsClient())
			{
				ItemStack toMake = new ItemStack(dataStream.readInt(), dataStream.readInt(), dataStream.readInt());
				int RecipeIndex = dataStream.readInt();
				IRecipe RecipeToMake = Zeldo.getCraftingRecipe(toMake);
				ContainerClevercraft.onRequestSingleRecipeOutput(Proxy.getPlayer(network), RecipeToMake, (TileEntityCraftingTableII)(Proxy.getPlayer(network).worldObj).getBlockTileEntity(dataStream.readInt(), dataStream.readInt(), dataStream.readInt()), RecipeIndex);
			}
			
			if (PacketID == kPacketTypeMaximumCraftingRequest && !Proxy.IsClient())
			{
				ItemStack toMake = new ItemStack(dataStream.readInt(), dataStream.readInt(), dataStream.readInt());
				int RecipeIndex = dataStream.readInt();
				IRecipe RecipeToMake = Zeldo.getCraftingRecipe(toMake);
				ContainerClevercraft.onRequestMaximumRecipeOutput(Proxy.getPlayer(network), RecipeToMake, (TileEntityCraftingTableII)(Proxy.getPlayer(network).worldObj).getBlockTileEntity(dataStream.readInt(), dataStream.readInt(), dataStream.readInt()), RecipeIndex);
			}
			if (PacketID == kPacketTypeUpdateItems && Proxy.IsClient())
			{
				Proxy.HandleUpdateItems();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        
	}
	
	public void SendCraftingPacket(ItemStack theItem, boolean Max, int xCoord, int yCoord, int zCoord, int RecipeIndex)
	{
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(bytes);
		try {
			if (Max)
				data.writeInt(kPacketTypeMaximumCraftingRequest);
			else
				data.writeInt(kPacketTypeSingleCraftingRequest);
			
			data.writeInt(theItem.itemID);
			data.writeInt(theItem.stackSize);
			data.writeInt(theItem.getItemDamage());
			data.writeInt(RecipeIndex);
			data.writeInt(xCoord);
			data.writeInt(yCoord);
			data.writeInt(zCoord);
			
			Packet250CustomPayload packet = new Packet250CustomPayload();
            packet.channel = ChannelName;
            packet.data = bytes.toByteArray();
            packet.length = packet.data.length;

            //
            Proxy.SendPacket(packet);
            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onConnect(NetworkManager network) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLogin(NetworkManager network, Packet1Login login) {
		MessageManager.getInstance().registerChannel(network, this, ChannelName);
	}

	@Override
	public void onDisconnect(NetworkManager network, String message, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	public Packet SendUpdatePacket() {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(bytes);
		try {
			data.writeInt(kPacketTypeUpdateItems);

			Packet250CustomPayload packet = new Packet250CustomPayload();
			packet.channel = ChannelName;
			packet.data = bytes.toByteArray();
			packet.length = packet.data.length;

			return packet;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
