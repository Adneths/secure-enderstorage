package me.adneths.secureenderstorage.block;

import java.util.ArrayList;
import java.util.List;

import codechicken.enderstorage.block.BlockEnderChest;
import codechicken.enderstorage.config.EnderStorageConfig;
import codechicken.enderstorage.tile.TileFrequencyOwner;
import codechicken.lib.colour.EnumColour;
import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.raytracer.SubHitBlockHitResult;
import codechicken.lib.util.ItemUtils;
import me.adneths.secureenderstorage.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class BlockSecureEnderChest extends BlockEnderChest
{
	public BlockSecureEnderChest(Properties properties)
	{
		super(properties);
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
	{
		List<ItemStack> drops = new ArrayList<>();
		TileFrequencyOwner tile = (TileFrequencyOwner) builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (tile != null)
		{
			if (!tile.getFrequency().hasOwner() || tile.getFrequency().owner.equals(builder.getOptionalParameter(LootContextParams.THIS_ENTITY).getUUID()))
				drops.addAll(super.getDrops(state, builder));
			else if (Config.OBSIDIAN.get())
				drops.add(new ItemStack(Items.OBSIDIAN, 2));
		}
		return drops;
	}

	@SuppressWarnings("deprecation")
	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter getter, BlockPos pos)
	{
		if (!Config.BREAKABLE.get())
		{
			TileFrequencyOwner tile = (TileFrequencyOwner) getter.getBlockEntity(pos);
			if (tile != null)
			{
				if (tile.getFrequency().hasOwner() && !tile.getFrequency().owner.equals(player.getUUID()))
					return 0f;
			}
		}
		return super.getDestroyProgress(state, player, getter, pos);
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, final BlockHitResult raytrace)
	{
		if (world.isClientSide)
		{
			return InteractionResult.SUCCESS;
		}
		BlockEntity tile = world.getBlockEntity(pos);
		if (!(tile instanceof TileFrequencyOwner))
		{
			return InteractionResult.FAIL;
		}
		TileFrequencyOwner owner = (TileFrequencyOwner) tile;

		// Normal block trace.
		final HitResult rawHit = RayTracer.retrace(player);
		if (rawHit instanceof SubHitBlockHitResult)
		{
			final SubHitBlockHitResult hit = (SubHitBlockHitResult) rawHit;
			if (hit.subHit == 4)
			{
				ItemStack item = player.getInventory().getSelected();
				if (player.isCrouching() && owner.getFrequency().hasOwner() && owner.getFrequency().owner.equals(player.getUUID()))
				{
					if (!player.getAbilities().instabuild && !player.getInventory().add(EnderStorageConfig.getPersonalItem().copy()))
					{
						return InteractionResult.FAIL;
					}

					owner.setFreq(owner.getFrequency().copy().setOwner(null));
					return InteractionResult.SUCCESS;
				}
				else if (!item.isEmpty() && ItemUtils.areStacksSameType(item, EnderStorageConfig.getPersonalItem()))
				{
					if (!owner.getFrequency().hasOwner())
					{
						owner.setFreq(owner.getFrequency().copy().setOwner(player.getUUID()).setOwnerName(player.getName()));
						if (!player.getAbilities().instabuild)
						{
							item.shrink(1);
						}
						return InteractionResult.SUCCESS;
					}
				}
			}
			else if (hit.subHit >= 1 && hit.subHit <= 3)
			{
				if (owner.getFrequency().hasOwner() && !owner.getFrequency().owner.equals(player.getUUID()))
				{
					player.sendSystemMessage(Component.literal(ChatFormatting.AQUA + "[SES] You do not own this ender storage"));
					return InteractionResult.FAIL;
				}
				ItemStack item = player.getInventory().getSelected();
				if (!item.isEmpty())
				{
					EnumColour dye = EnumColour.fromDyeStack(item);
					if (dye != null)
					{
						EnumColour[] colours = { null, null, null };
						if (colours[hit.subHit - 1] == dye)
						{
							return InteractionResult.FAIL;
						}
						colours[hit.subHit - 1] = dye;
						owner.setFreq(owner.getFrequency().copy().set(colours));
						if (!player.getAbilities().instabuild)
						{
							item.shrink(1);
						}
						return InteractionResult.FAIL;
					}
				}
			}
			if (Config.LOCKED.get() && owner.getFrequency().hasOwner() && !owner.getFrequency().owner.equals(player.getUUID()))
			{
				player.sendSystemMessage(Component.literal(ChatFormatting.AQUA + "[SES] You do not own this ender storage"));
				return InteractionResult.FAIL;
			}
			return !player.isCrouching() && owner.activate(player, hit.subHit, hand) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
		}
        return InteractionResult.FAIL;
	}

}
