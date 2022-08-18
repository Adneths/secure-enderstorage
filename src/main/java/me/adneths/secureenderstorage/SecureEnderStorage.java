package me.adneths.secureenderstorage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.adneths.secureenderstorage.init.ModContents;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SecureEnderStorage.MODID)
public class SecureEnderStorage
{
	public static final String MODID = "secureenderstorage";

	public static final Logger logger = LogManager.getLogger("SecureEnderStorage");

	public SecureEnderStorage()
	{
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.CONFIG);
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	@SubscribeEvent
	public void onCommonSetup(FMLCommonSetupEvent event)
	{
		logger.info("Loading SecureEnderStorage");
		ModContents.init();
	}

	@SubscribeEvent
	public void onClientSetup(FMLClientSetupEvent event)
	{
	}

	@SubscribeEvent
	public void onServerSetup(FMLDedicatedServerSetupEvent event)
	{
	}

}
