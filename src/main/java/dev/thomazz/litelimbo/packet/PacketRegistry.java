package dev.thomazz.litelimbo.packet;

import dev.thomazz.litelimbo.packet.impl.Disconnect;
import dev.thomazz.litelimbo.packet.impl.Handshake;
import dev.thomazz.litelimbo.packet.impl.game.Chat;
import dev.thomazz.litelimbo.packet.impl.game.JoinGame;
import dev.thomazz.litelimbo.packet.impl.game.KeepAlive;
import dev.thomazz.litelimbo.packet.impl.game.Position;
import dev.thomazz.litelimbo.packet.impl.login.EncryptionRequest;
import dev.thomazz.litelimbo.packet.impl.login.EncryptionResponse;
import dev.thomazz.litelimbo.packet.impl.login.ServerLogin;
import dev.thomazz.litelimbo.packet.impl.login.ServerLoginSuccess;
import dev.thomazz.litelimbo.packet.impl.login.SetCompression;
import dev.thomazz.litelimbo.packet.impl.status.StatusPing;
import dev.thomazz.litelimbo.packet.impl.status.StatusRequest;
import dev.thomazz.litelimbo.packet.impl.status.StatusResponse;
import dev.thomazz.litelimbo.packet.property.Direction;
import dev.thomazz.litelimbo.packet.property.Protocol;
import dev.thomazz.litelimbo.packet.property.Version;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.function.Supplier;
import lombok.Data;
import sun.misc.SharedSecrets;

public final class PacketRegistry {
	private static VersionMapping CLIENT_BOUND = new VersionMapping();
	private static VersionMapping SERVER_BOUND = new VersionMapping();

	public static void init() {
		PacketRegistry.SERVER_BOUND.register(Protocol.HANDSHAKE, Handshake.class, Handshake::new,
				new PacketVersionEntry(0x00, Version.MINECRAFT_1_7_2)
		);

		PacketRegistry.SERVER_BOUND.register(Protocol.STATUS, StatusRequest.class, StatusRequest::new,
				new PacketVersionEntry(0x00, Version.MINECRAFT_1_7_2)
		);
		PacketRegistry.SERVER_BOUND.register(Protocol.STATUS, StatusPing.class, StatusPing::new,
				new PacketVersionEntry(0x01, Version.MINECRAFT_1_7_2)
		);
		PacketRegistry.CLIENT_BOUND.register(Protocol.STATUS, StatusResponse.class, StatusResponse::new,
				new PacketVersionEntry(0x00, Version.MINECRAFT_1_7_2)
		);
		PacketRegistry.CLIENT_BOUND.register(Protocol.STATUS, StatusPing.class, StatusPing::new,
				new PacketVersionEntry(0x01, Version.MINECRAFT_1_7_2)
		);

		PacketRegistry.SERVER_BOUND.register(Protocol.LOGIN, ServerLogin.class, ServerLogin::new,
				new PacketVersionEntry(0x00, Version.MINECRAFT_1_7_2)
		);
		PacketRegistry.SERVER_BOUND.register(Protocol.LOGIN, EncryptionResponse.class, EncryptionResponse::new,
				new PacketVersionEntry(0x01, Version.MINECRAFT_1_7_2)
		);
		PacketRegistry.CLIENT_BOUND.register(Protocol.LOGIN, Disconnect.class, Disconnect::new,
				new PacketVersionEntry(0x00, Version.MINECRAFT_1_7_2)
		);
		PacketRegistry.CLIENT_BOUND.register(Protocol.LOGIN, EncryptionRequest.class, EncryptionRequest::new,
				new PacketVersionEntry(0x01, Version.MINECRAFT_1_7_2)
		);
		PacketRegistry.CLIENT_BOUND.register(Protocol.LOGIN, ServerLoginSuccess.class, ServerLoginSuccess::new,
				new PacketVersionEntry(0x02, Version.MINECRAFT_1_7_2)
		);
		PacketRegistry.CLIENT_BOUND.register(Protocol.LOGIN, SetCompression.class, SetCompression::new,
				new PacketVersionEntry(0x03, Version.MINECRAFT_1_8)
		);

		PacketRegistry.SERVER_BOUND.register(Protocol.PLAY, Chat.class, Chat::new,
				new PacketVersionEntry(0x01, Version.MINECRAFT_1_7_2),
				new PacketVersionEntry(0x02, Version.MINECRAFT_1_9),
				new PacketVersionEntry(0x03, Version.MINECRAFT_1_12),
				new PacketVersionEntry(0x02, Version.MINECRAFT_1_12_1),
				new PacketVersionEntry(0x03, Version.MINECRAFT_1_14)
		);
		PacketRegistry.CLIENT_BOUND.register(Protocol.PLAY, KeepAlive.class, KeepAlive::new,
				new PacketVersionEntry(0x00, Version.MINECRAFT_1_7_2),
				new PacketVersionEntry(0x1F, Version.MINECRAFT_1_9),
				new PacketVersionEntry(0x21, Version.MINECRAFT_1_13),
				new PacketVersionEntry(0x20, Version.MINECRAFT_1_14),
				new PacketVersionEntry(0x21, Version.MINECRAFT_1_15),
				new PacketVersionEntry(0x20, Version.MINECRAFT_1_16),
				new PacketVersionEntry(0x1F, Version.MINECRAFT_1_16_2),
				new PacketVersionEntry(0x21, Version.MINECRAFT_1_17)
		);
		PacketRegistry.CLIENT_BOUND.register(Protocol.PLAY, Chat.class, Chat::new,
				new PacketVersionEntry(0x02, Version.MINECRAFT_1_7_2),
				new PacketVersionEntry(0x0F, Version.MINECRAFT_1_9),
				new PacketVersionEntry(0x0E, Version.MINECRAFT_1_13),
				new PacketVersionEntry(0x0F, Version.MINECRAFT_1_15),
				new PacketVersionEntry(0x0E, Version.MINECRAFT_1_16),
				new PacketVersionEntry(0x0F, Version.MINECRAFT_1_17)
		);
		PacketRegistry.CLIENT_BOUND.register(Protocol.PLAY, Disconnect.class, Disconnect::new,
				new PacketVersionEntry(0x40, Version.MINECRAFT_1_7_2),
				new PacketVersionEntry(0x1A, Version.MINECRAFT_1_9),
				new PacketVersionEntry(0x1B, Version.MINECRAFT_1_13),
				new PacketVersionEntry(0x1A, Version.MINECRAFT_1_14),
				new PacketVersionEntry(0x1B, Version.MINECRAFT_1_15),
				new PacketVersionEntry(0x1A, Version.MINECRAFT_1_16),
				new PacketVersionEntry(0x19, Version.MINECRAFT_1_16_2),
				new PacketVersionEntry(0x1A, Version.MINECRAFT_1_17)
		);
		PacketRegistry.CLIENT_BOUND.register(Protocol.PLAY, JoinGame.class, JoinGame::new,
				new PacketVersionEntry(0x01, Version.MINECRAFT_1_7_2),
				new PacketVersionEntry(0x23, Version.MINECRAFT_1_9),
				new PacketVersionEntry(0x25, Version.MINECRAFT_1_13),
				new PacketVersionEntry(0x25, Version.MINECRAFT_1_14),
				new PacketVersionEntry(0x26, Version.MINECRAFT_1_15),
				new PacketVersionEntry(0x25, Version.MINECRAFT_1_16),
				new PacketVersionEntry(0x24, Version.MINECRAFT_1_16_2),
				new PacketVersionEntry(0x26, Version.MINECRAFT_1_17)
		);
		PacketRegistry.CLIENT_BOUND.register(Protocol.PLAY, Position.class, Position::new,
				new PacketVersionEntry(0x08, Version.MINECRAFT_1_7_2),
				new PacketVersionEntry(0x2E, Version.MINECRAFT_1_9),
				new PacketVersionEntry(0x2F, Version.MINECRAFT_1_12_1),
				new PacketVersionEntry(0x32, Version.MINECRAFT_1_13),
				new PacketVersionEntry(0x35, Version.MINECRAFT_1_14),
				new PacketVersionEntry(0x36, Version.MINECRAFT_1_15),
				new PacketVersionEntry(0x34, Version.MINECRAFT_1_16)
		);
	}

	public static PacketProvider getProvider(Direction direction, Version version, Protocol protocol) {
		VersionMapping mapping;
		switch (direction) {
			default:
			case CLIENTBOUND:
				mapping = PacketRegistry.CLIENT_BOUND;
				break;
			case SERVERBOUND:
				mapping = PacketRegistry.SERVER_BOUND;
				break;
		}

		return mapping.versionToProtocol.get(version).protocolToProvider.get(protocol);
	}

	public static class VersionMapping {
		private Map<Version, ProtocolMapping> versionToProtocol = PacketRegistry.createEnumMap(Version.class, ProtocolMapping::new);

		private <T extends Packet> void register(Protocol protocol, Class<T> type, Supplier<T> supplier, PacketVersionEntry... versionEntries) {
			for (int i = 0; i < versionEntries.length; i++) {
				PacketVersionEntry current = versionEntries[i];
				PacketVersionEntry next = versionEntries[Math.min(i + 1, versionEntries.length - 1)];
				Version from = current.version;
				Version to = current == next ? Version.latest() : next.version;

				for (Version version : EnumSet.range(from, to)) {
					this.versionToProtocol.get(version).map(protocol, current.packetId, type, supplier);
				}
			}
		}
	}

	public static class ProtocolMapping {
		private Map<Protocol, PacketProvider> protocolToProvider = PacketRegistry.createEnumMap(Protocol.class, PacketProvider::new);

		private <T extends Packet> void map(Protocol protocol, int packetId, Class<T> type, Supplier<T> supplier) {
			this.protocolToProvider.get(protocol).register(packetId, type, supplier);
		}
	}

	private static <K extends Enum<K>, V> Map<K, V> createEnumMap(Class<K> clazz, Supplier<V> valueSupplier) {
		Map<K, V> map = new EnumMap<>(clazz);
		K[] values = SharedSecrets.getJavaLangAccess().getEnumConstantsShared(clazz);
		Arrays.stream(values).forEach(version -> map.put(version, valueSupplier.get()));
		return map;
	}

	@Data
	private static class PacketVersionEntry {
		private final int packetId;
		private final Version version;
	}
}


