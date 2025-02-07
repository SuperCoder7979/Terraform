package com.terraformersmc.terraform.entity;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class TerraformBoatEntity extends BoatEntity {
	private TerraformBoat boat;

	public TerraformBoatEntity(EntityType<? extends TerraformBoatEntity> type, World world, TerraformBoat boat) {
		super(type, world);

		this.boat = boat;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
	}

	@Override
	public Item asItem() {
		return boat.asItem();
	}

	public Item asPlanks() {
		return boat.asPlanks();
	}

	public Identifier getBoatSkin() {
		return boat.getSkin();
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag tag) {}

	@Override
	protected void readCustomDataFromTag(CompoundTag tag) {}

	private boolean isOnLand() {
		// super hackish way of evaluating the condition (this.location == BoatEntity.Location.ON_LAND)

		return getPaddleSoundEvent() == SoundEvents.ENTITY_BOAT_PADDLE_LAND;
	}

	@Override
	protected void fall(double double_1, boolean boolean_1, BlockState state, BlockPos pos) {

		float savedFallDistance = this.fallDistance;

		// Run other logic, including setting the private field fallVelocity
		super.fall(double_1, false, state, pos);

		if(!this.hasVehicle() && boolean_1) {
			this.fallDistance = savedFallDistance;

			if (this.fallDistance > 3.0F) {
				if (!isOnLand()) {
					this.fallDistance = 0.0F;
					return;
				}

				this.handleFallDamage(this.fallDistance, 1.0F);
				if (!this.world.isClient && !this.removed) {
					this.remove();
					if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
						for(int i = 0; i < 3; i++) {
							this.dropItem(this.asPlanks());
						}

						for(int i = 0; i < 2; i++) {
							this.dropItem(Items.STICK);
						}
					}
				}
			}

			this.fallDistance = 0.0F;
		}
	}

	@Override
	public Packet<?> createSpawnPacket() {
		final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

		buf.writeVarInt(this.getEntityId());
		buf.writeUuid(this.uuid);
		buf.writeVarInt(Registry.ENTITY_TYPE.getRawId(this.getType()));
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeByte(MathHelper.floor(this.pitch * 256.0F / 360.0F));
		buf.writeByte(MathHelper.floor(this.yaw * 256.0F / 360.0F));

		return ServerSidePacketRegistry.INSTANCE.toPacket(new Identifier("terraform", "spawn_boat"), buf);
	}

	@Override
	public void setBoatType(BoatEntity.Type type) {
		throw new UnsupportedOperationException("Tried to set the boat type of a Terraform boat");
	}

	@Override
	public BoatEntity.Type getBoatType() {
		return boat.getVanillaType();
	}
}
