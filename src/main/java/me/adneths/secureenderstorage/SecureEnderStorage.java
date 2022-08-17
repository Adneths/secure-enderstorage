package me.adneths.secureenderstorage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.adneths.secureenderstorage.proxy.ClientProxy;
import me.adneths.secureenderstorage.proxy.Proxy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
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

	public static Proxy proxy;

	public SecureEnderStorage()
	{
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.CONFIG);
		proxy = (Proxy) DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> Proxy::new);
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	@SubscribeEvent
	public void onCommonSetup(FMLCommonSetupEvent event)
	{
		logger.info("Loading SecureEnderStorage");
		proxy.commonSetup(event);
	}

	@SubscribeEvent
	public void onClientSetup(FMLClientSetupEvent event)
	{
		proxy.clientSetup(event);
	}

	@SubscribeEvent
	public void onServerSetup(FMLDedicatedServerSetupEvent event)
	{
	}

}
