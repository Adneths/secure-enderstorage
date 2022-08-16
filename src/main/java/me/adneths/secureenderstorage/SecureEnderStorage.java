package me.adneths.secureenderstorage;

import org.apache.logging.log4j.Logger;

import me.adneths.secureenderstorage.proxy.IProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = SecureEnderStorage.MODID, name = SecureEnderStorage.NAME, version = SecureEnderStorage.VERSION, dependencies = SecureEnderStorage.DEPENDENCIES)
public class SecureEnderStorage {
	
	public static final String MODID = "secureenderstorage";
	public static final String NAME = "Secure Ender Storage";
	public static final String VERSION = "1.12.2-1.2.0";
	public static final String DEPENDENCIES = "required-after:enderstorage@[2.4.6.137,)";

	public static Logger logger;

	@SidedProxy (clientSide = "me.adneths.secureenderstorage.proxy.ClientProxy", serverSide = "me.adneths.secureenderstorage.proxy.ServerProxy")
	public static IProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit();
		logger.info("SecureEnderStorage finished loading");
	}

}
