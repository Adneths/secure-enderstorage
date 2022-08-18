package me.adneths.secureenderstorage.item;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.config.EnderStorageConfig;
import codechicken.enderstorage.item.ItemEnderPouch;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.enderstorage.tile.TileEnderChest;
import me.adneths.secureenderstorage.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ItemSecureEnderPouch extends ItemEnderPouch
{

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
	{
		final Level world = context.getLevel();
		if (world.isClientSide)
			return InteractionResult.PASS;

		BlockEntity tile = world.getBlockEntity(context.getClickedPos());
		if (tile instanceof TileEnderChest && context.getPlayer().isCrouching())
		{
			TileEnderChest chest = (TileEnderChest) tile;
			Frequency frequency = chest.getFrequency().copy();
			if (EnderStorageConfig.anarchyMode && (frequency.owner == null || !frequency.owner.equals(context.getPlayer().getUUID())))
				frequency.setOwner(null);

			if (!frequency.hasOwner() || frequency.owner.equals(context.getPlayer().getUUID()) || EnderStorageConfig.anarchyMode)
			{
				frequency.writeToStack(stack);
				return InteractionResult.SUCCESS;
			} else
				context.getPlayer().sendMessage(new TextComponent(ChatFormatting.AQUA + "[SES] You do not own this ender storage"), context.getPlayer().getUUID());
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
	{
		ItemStack stack = player.getItemInHand(hand);
		if (player.isCrouching())
			return new InteractionResultHolder(InteractionResult.PASS, stack);
		if (!world.isClientSide)
		{
			Frequency frequency = Frequency.readFromStack(stack);
			if (!frequency.hasOwner() || frequency.owner.equals(player.getUUID()) || !Config.LOCKED.get())
				EnderStorageManager.instance(world.isClientSide).getStorage(frequency, EnderItemStorage.TYPE).openContainer((ServerPlayer)player, new TranslatableComponent(stack.getDescriptionId()));
			else
				player.sendMessage(new TextComponent(ChatFormatting.AQUA + "[SES] You do not own this ender storage"), player.getUUID());
		}
		return new InteractionResultHolder(InteractionResult.SUCCESS, stack);
	}

}
