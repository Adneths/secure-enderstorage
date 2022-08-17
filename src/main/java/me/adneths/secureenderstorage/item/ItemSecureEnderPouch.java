package me.adneths.secureenderstorage.item;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.config.EnderStorageConfig;
import codechicken.enderstorage.item.ItemEnderPouch;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.enderstorage.tile.TileEnderChest;
import me.adneths.secureenderstorage.Config;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemSecureEnderPouch extends ItemEnderPouch
{

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context)
	{
		World world = context.getLevel();
		if (world.isClientSide)
			return ActionResultType.PASS;

		TileEntity tile = world.getBlockEntity(context.getClickedPos());
		if (tile instanceof TileEnderChest && context.getPlayer().isCrouching())
		{
			TileEnderChest chest = (TileEnderChest) tile;
			Frequency frequency = chest.getFrequency().copy();
			if (EnderStorageConfig.anarchyMode && (frequency.owner == null || !frequency.owner.equals(context.getPlayer().getUUID())))
				frequency.setOwner(null);

			if (!frequency.hasOwner() || frequency.owner.equals(context.getPlayer().getUUID()) || EnderStorageConfig.anarchyMode)
			{
				frequency.writeToStack(stack);
				return ActionResultType.SUCCESS;
			} else
				context.getPlayer().sendMessage(new StringTextComponent(TextFormatting.AQUA + "[SES] You do not own this ender storage"), context.getPlayer().getUUID());
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
	{
		ItemStack stack = player.getItemInHand(hand);
		if (player.isCrouching())
			return new ActionResult(ActionResultType.PASS, stack);
		if (!world.isClientSide)
		{
			Frequency frequency = Frequency.readFromStack(stack);
			if (!frequency.hasOwner() || frequency.owner.equals(player.getUUID()) || !Config.LOCKED.get())
				((EnderItemStorage)EnderStorageManager.instance(world.isClientSide).getStorage(frequency, EnderItemStorage.TYPE)).openContainer((ServerPlayerEntity)player, (ITextComponent)new TranslationTextComponent(stack.getDescriptionId(), new Object[0]));
			else
				player.sendMessage(new StringTextComponent(TextFormatting.AQUA + "[SES] You do not own this ender storage"), player.getUUID());
		}
		return new ActionResult(ActionResultType.SUCCESS, stack);
	}

}
