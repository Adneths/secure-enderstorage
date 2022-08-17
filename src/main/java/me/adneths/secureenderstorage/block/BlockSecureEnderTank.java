package me.adneths.secureenderstorage.block;

import java.util.ArrayList;
import java.util.List;

import codechicken.enderstorage.block.BlockEnderTank;
import codechicken.enderstorage.config.EnderStorageConfig;
import codechicken.enderstorage.tile.TileFrequencyOwner;
import codechicken.lib.colour.EnumColour;
import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.util.ItemUtils;
import me.adneths.secureenderstorage.Config;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;

public class BlockSecureEnderTank extends BlockEnderTank
{
	public BlockSecureEnderTank(Properties properties)
	{
		super(properties);
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
	{
		List<ItemStack> drops = new ArrayList<>();
		TileFrequencyOwner tile = (TileFrequencyOwner) builder.assertPresent(LootParameters.BLOCK_ENTITY);
		if (tile != null)
		{
			if (!tile.getFrequency().hasOwner() || tile.getFrequency().owner.equals(builder.assertPresent(LootParameters.THIS_ENTITY).getUniqueID()))
				drops.addAll(super.getDrops(state, builder));
			else if (Config.OBSIDIAN.get())
				drops.add(new ItemStack(Item.getItemFromBlock(Blocks.OBSIDIAN), 2));
		}
		return drops;
	}

	@SuppressWarnings("deprecation")
	@Override
	public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader world, BlockPos pos)
	{
		if (!Config.BREAKABLE.get())
		{
			TileFrequencyOwner tile = (TileFrequencyOwner) world.getTileEntity(pos);
			if (tile != null)
			{
				if (tile.getFrequency().hasOwner() && !tile.getFrequency().owner.equals(player.getUniqueID()))
					return 0f;
			}
		}
		return super.getPlayerRelativeBlockHardness(state, player, world, pos);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult raytrace)
	{
		if (world.isRemote)
		{
			return ActionResultType.SUCCESS;
		}
		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof TileFrequencyOwner))
		{
			return ActionResultType.FAIL;
		}
		TileFrequencyOwner owner = (TileFrequencyOwner) tile;

		// Normal block trace.
		RayTraceResult hit = RayTracer.retraceBlock(world, player, pos);
		if (hit == null)
		{
			return ActionResultType.FAIL;
		}
		if (hit.subHit == 4)
		{
			ItemStack item = player.inventory.getCurrentItem();
			if (player.isSneaking() && owner.getFrequency().hasOwner() && owner.getFrequency().owner.equals(player.getUniqueID()))
			{
				if (!player.abilities.isCreativeMode && !player.inventory.addItemStackToInventory(EnderStorageConfig.personalItem.copy()))
				{
					return ActionResultType.FAIL;
				}

				owner.setFreq(owner.getFrequency().copy().setOwner(null));
				return ActionResultType.SUCCESS;
			}
			else if (!item.isEmpty() && ItemUtils.areStacksSameOrTagged(item, EnderStorageConfig.personalItem))
			{
				if (!owner.getFrequency().hasOwner())
				{
					owner.setFreq(owner.getFrequency().copy().setOwner(player.getUniqueID()).setOwnerName(player.getName()));
					if (!player.abilities.isCreativeMode)
					{
						item.shrink(1);
					}
					return ActionResultType.SUCCESS;
				}
			}
		}
		else if (hit.subHit >= 1 && hit.subHit <= 3)
		{
			if (owner.getFrequency().hasOwner() && !owner.getFrequency().owner.equals(player.getUniqueID()))
			{
				player.sendMessage(new StringTextComponent(TextFormatting.AQUA + "[SES] You do not own this ender storage"));
				return ActionResultType.FAIL;
			}
			ItemStack item = player.inventory.getCurrentItem();
			if (!item.isEmpty())
			{
				EnumColour dye = EnumColour.fromDyeStack(item);
				if (dye != null)
				{
					EnumColour[] colours = { null, null, null };
					if (colours[hit.subHit - 1] == dye)
					{
						return ActionResultType.FAIL;
					}
					colours[hit.subHit - 1] = dye;
					owner.setFreq(owner.getFrequency().copy().set(colours));
					if (!player.abilities.isCreativeMode)
					{
						item.shrink(1);
					}
					return ActionResultType.FAIL;
				}
			}
		}
		if (Config.LOCKED.get() && owner.getFrequency().hasOwner() && !owner.getFrequency().owner.equals(player.getUniqueID()))
		{
			player.sendMessage(new StringTextComponent(TextFormatting.AQUA + "[SES] You do not own this ender storage"));
			return ActionResultType.FAIL;
		}
		return !player.isCrouching() && owner.activate(player, hit.subHit, hand) ? ActionResultType.SUCCESS : ActionResultType.FAIL;
	}

}
