package me.adneths.secureenderstorage.init;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.ModelBakery;
import me.adneths.secureenderstorage.SecureEnderStorage;
import me.adneths.secureenderstorage.block.SecureEnderStorageBlock;
import me.adneths.secureenderstorage.item.SecureEnderPouchItem;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@ObjectHolder (SecureEnderStorage.MODID)
@Mod.EventBusSubscriber (modid = SecureEnderStorage.MODID)
public class ModContents {

	@ObjectHolder("enderstorage:ender_storage")
	public static SecureEnderStorageBlock secureEnderStorageBlock;
	
	@ObjectHolder("enderstorage:ender_pouch")
	public static SecureEnderPouchItem secureEnderPouchItem;

	
	@SubscribeEvent
	public static void onRegisterBlocks(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> registry = event.getRegistry();
		SecureEnderStorage.logger.info("Registering SecureEnderStorageBlock");
		registry.register(new SecureEnderStorageBlock().setRegistryName("enderstorage","ender_storage"));
	}

	@SubscribeEvent
	public static void onRegisterItems(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		SecureEnderStorage.logger.info("Registering SecureEnderStorageItem");
		registry.register(new SecureEnderPouchItem().setRegistryName("enderstorage","ender_pouch"));
	}
	
	@SideOnly (Side.CLIENT)
	public static void registerModels() {
		ModelResourceLocation invLocation = new ModelResourceLocation("enderstorage:ender_pouch", "inventory");
		ModelLoader.setCustomModelResourceLocation(secureEnderPouchItem, 0, invLocation);
		ModelLoader.setCustomMeshDefinition(secureEnderPouchItem, (stack) -> invLocation);
		ModelRegistryHelper.register(invLocation, new CCBakeryModel());
		ModelBakery.registerItemKeyGenerator(secureEnderPouchItem, stack -> {
			Frequency frequency = Frequency.readFromStack(stack);
			boolean open = ((EnderItemStorage) EnderStorageManager.instance(true).getStorage(frequency, "item")).openCount() > 0;
			return ModelBakery.defaultItemKeyGenerator.generateKey(stack) + "|" + frequency.toModelLoc() + "|" + open;
		});
	}
	
}
