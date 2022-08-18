package me.adneths.secureenderstorage.init;

import codechicken.enderstorage.block.BlockEnderChest;
import codechicken.enderstorage.block.BlockEnderTank;
import codechicken.enderstorage.item.ItemEnderPouch;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModContents
{
	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "enderstorage");
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "enderstorage");

	
	public static final BlockBehaviour.Properties props = BlockBehaviour.Properties.of(Material.STONE).strength(20.0f, 100.0f);
	public static final RegistryObject<BlockEnderChest> ENDER_CHEST_BLOCK = BLOCKS.register("ender_chest", () -> new BlockEnderChest(props));
	public static final RegistryObject<BlockEnderTank> ENDER_TANK_BLOCK = BLOCKS.register("ender_tank", () -> new BlockEnderTank(props));
	public static final RegistryObject<ItemEnderPouch> ENDER_POUCH = ITEMS.register("ender_pouch", ItemEnderPouch::new);

	public static void init()
	{
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		BLOCKS.register(bus);
	}

}
