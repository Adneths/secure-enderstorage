package me.adneths.secureenderstorage.init;

import me.adneths.secureenderstorage.SecureEnderStorage;
import me.adneths.secureenderstorage.block.BlockSecureEnderChest;
import me.adneths.secureenderstorage.block.BlockSecureEnderTank;
import me.adneths.secureenderstorage.item.ItemSecureEnderPouch;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber (modid = SecureEnderStorage.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModContents {
	
	@SubscribeEvent
	public static void onRegisterBlocks(RegistryEvent.Register<Block> event) {
		SecureEnderStorage.logger.info("Registering Blocks");
		IForgeRegistry<Block> registry = event.getRegistry();
	    Block.Properties properties = Block.Properties.create(Material.ROCK).hardnessAndResistance(20.0F, 100.0F);
		registry.register(new BlockSecureEnderChest(properties).setRegistryName("enderstorage","ender_chest"));
		registry.register(new BlockSecureEnderTank(properties).setRegistryName("enderstorage","ender_tank"));
	}

	@SubscribeEvent
	public static void onRegisterItems(RegistryEvent.Register<Item> event) {
		SecureEnderStorage.logger.info("Registering Items");
		IForgeRegistry<Item> registry = event.getRegistry();
		registry.register(new ItemSecureEnderPouch().setRegistryName("enderstorage","ender_pouch"));
	}
	
}
