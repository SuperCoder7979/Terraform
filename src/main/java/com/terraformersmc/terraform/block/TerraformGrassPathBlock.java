package com.terraformersmc.terraform.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.GrassPathBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class TerraformGrassPathBlock extends GrassPathBlock {
	private Block dirt;

	public TerraformGrassPathBlock(Block dirt, Settings settings) {
		super(settings);

		this.dirt = dirt;
	}

	public void onScheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		world.setBlockState(pos, pushEntitiesUpBeforeBlockChange(state, dirt.getDefaultState(), world, pos));
	}
}
