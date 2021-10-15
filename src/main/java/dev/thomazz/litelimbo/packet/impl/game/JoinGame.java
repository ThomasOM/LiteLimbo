package dev.thomazz.litelimbo.packet.impl.game;

import dev.thomazz.litelimbo.packet.Packet;
import dev.thomazz.litelimbo.packet.property.Version;
import dev.thomazz.litelimbo.packet.property.Direction;
import dev.thomazz.litelimbo.util.MinecraftBufferReader;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JoinGame implements Packet {
	private int entityId;
	private short gamemode;
	private int dimension;
	private long partialHashedSeed;
	private short difficulty;
	private short maxPlayers;
	private String levelType;
	private int viewDistance;
	private boolean reducedDebugInfo;
	private boolean showRespawnScreen;

	// FIXME: 1.16+ Implementation
	@Override
	public void write(ByteBuf buf, Version version, Direction direction) {
		buf.writeInt(this.entityId);

		if (version.compareTo(Version.MINECRAFT_1_16_2) >= 0) {
			buf.writeBoolean(false);
		}

		buf.writeByte(this.gamemode);

		if (version.compareTo(Version.MINECRAFT_1_16) >= 0) {
			buf.writeByte(this.gamemode);

			MinecraftBufferReader.writeStringArray(buf, new String[]{"minecraft:the_end"});
			CompoundBinaryTag.Builder registryContainer = CompoundBinaryTag.builder();
			ListBinaryTag encodedDimensionRegistry = this.encodeRegistry();

			if (version.compareTo(Version.MINECRAFT_1_16_2) >= 0) {
				CompoundBinaryTag.Builder dimensionRegistryEntry = CompoundBinaryTag.builder();
				dimensionRegistryEntry.putString("type", "minecraft:dimension_type");
				dimensionRegistryEntry.put("value", encodedDimensionRegistry);
				registryContainer.put("minecraft:dimension_type", dimensionRegistryEntry.build());

				CompoundBinaryTag.Builder biomeEntry = CompoundBinaryTag.builder();
				biomeEntry.putString("type", "minecraft:worldgen/biome");
				biomeEntry.put("value", this.encodeBiomes());
				registryContainer.put("minecraft:worldgen/biome", biomeEntry.build());
			} else {
				registryContainer.put("dimension", encodedDimensionRegistry);
			}

			MinecraftBufferReader.writeCompoundTag(buf, registryContainer.build());

			if (version.compareTo(Version.MINECRAFT_1_16_2) >= 0) {
				MinecraftBufferReader.writeCompoundTag(buf, CompoundBinaryTag.empty());
				MinecraftBufferReader.writeString(buf, "empty");
			} else {
				MinecraftBufferReader.writeString(buf, "empty");
				MinecraftBufferReader.writeString(buf, "empty");
			}
		} else if (version.compareTo(Version.MINECRAFT_1_9_1) >= 0) {
			buf.writeInt(this.dimension);
		} else {
			buf.writeByte(this.dimension);
		}

		if (version.compareTo(Version.MINECRAFT_1_13_2) <= 0) {
			buf.writeByte(this.difficulty);
		}

		if (version.compareTo(Version.MINECRAFT_1_15) >= 0) {
			buf.writeLong(this.partialHashedSeed);
		}

		if (version.compareTo(Version.MINECRAFT_1_16_2) >= 0) {
			MinecraftBufferReader.writeVarInt(buf, maxPlayers);
		} else {
			buf.writeByte(this.maxPlayers);
		}

		if (version.compareTo(Version.MINECRAFT_1_16) < 0) {
			if (this.levelType == null) {
				throw new IllegalStateException("No level type specified.");
			}

			MinecraftBufferReader.writeString(buf, this.levelType);
		}

		if (version.compareTo(Version.MINECRAFT_1_14) >= 0) {
			MinecraftBufferReader.writeVarInt(buf, viewDistance);
		}

		if (version.compareTo(Version.MINECRAFT_1_8) >= 0) {
			buf.writeBoolean(this.reducedDebugInfo);
		}

		if (version.compareTo(Version.MINECRAFT_1_15) >= 0) {
			buf.writeBoolean(this.showRespawnScreen);
		}

		if (version.compareTo(Version.MINECRAFT_1_16) >= 0) {
			buf.writeBoolean(false);
			buf.writeBoolean(false);
		}
	}

	private ListBinaryTag encodeRegistry() {
		ListBinaryTag.Builder<CompoundBinaryTag> listBuilder = ListBinaryTag
				.builder(BinaryTagTypes.COMPOUND);
		return listBuilder.add(this.emptyDimension()).build();
	}

	private CompoundBinaryTag emptyDimension() {
		return CompoundBinaryTag.builder()
				.putString("name", "minecraft:the_end")
				.putInt("id", 3)
				.put("element", CompoundBinaryTag.builder().put("element", this.emptyDimensionData()).build())
				.build();
	}

	private CompoundBinaryTag emptyDimensionData() {
		return CompoundBinaryTag.builder()
				.putBoolean("piglin_safe", false)
				.putBoolean("natural", false)
				.putFloat("ambient_light", 0.0F)
				.putString("infiniburn", "minecraft:infiniburn_end")
				.putBoolean("respawn_anchor_works", false)
				.putBoolean("has_skylight", false)
				.putBoolean("bed_works", false)
				.putString("effects", "minecraft:the_end")
				.putLong("fixed_time", 0L)
				.putBoolean("has_raids", false)
				.putDouble("coordinate_scale", 1.0)
				.putInt("logical_height", 256)
				.putBoolean("ultrawarm", false)
				.putBoolean("has_ceiling", false)
				.build();
	}

	private ListBinaryTag encodeBiomes() {
		ListBinaryTag.Builder<CompoundBinaryTag> listBuilder = ListBinaryTag
				.builder(BinaryTagTypes.COMPOUND);
		return listBuilder.add(this.emptyBiome()).build();
	}

	private CompoundBinaryTag emptyBiome() {
		return CompoundBinaryTag.builder()
				.putString("name", "minecraft:the_end")
				.putInt("id", 0)
				.put("element", CompoundBinaryTag.builder().put("element", this.emptyBiomeData()).build())
				.build();
	}

	private CompoundBinaryTag emptyBiomeData() {
		return CompoundBinaryTag.builder()
				.putString("precipitation", "none")
				.put("effects", this.emptyBiomeEffects())
				.putFloat("depth", 0.1F)
				.putFloat("temperature", 0.5F)
				.putFloat("scale", 0.2F)
				.putFloat("downfall", 0.5F)
				.putString("category", "\"the end\"")
				.putLong("fixed_time", 0L)
				.putBoolean("has_raids", false)
				.putDouble("coordinate_scale", 1.0)
				.putInt("logical_height", 256)
				.putBoolean("ultrawarm", false)
				.putBoolean("has_ceiling", false)
				.build();
	}

	private CompoundBinaryTag emptyBiomeEffects() {
		return CompoundBinaryTag.builder()
				.putInt("sky_color", 0)
				.putInt("water_fog_color", 0)
				.putInt("fog_color", 0)
				.putInt("water_color", 0)
				.build();
	}
}
