package dev.thomazz.litelimbo.packet;

import com.carrotsearch.hppc.ObjectIntIdentityHashMap;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import com.carrotsearch.hppc.ObjectIntMap;
import java.util.function.Supplier;
import lombok.Data;

public class PacketProvider {
	private final IntObjectMap<PacketEntry<?>> packetMapping = new IntObjectHashMap<>();
	private final ObjectIntMap<Class<? extends Packet>> typeMapping = new ObjectIntIdentityHashMap<>(128);

	public <T extends Packet> void register(int id, Class<T> type, Supplier<T> supplier) {
		this.packetMapping.put(id, new PacketEntry<>(type, supplier));
		this.typeMapping.put(type, id);
	}

	public int getId(Packet packet) {
		int id = this.typeMapping.getOrDefault(packet.getClass(), -1);

		if (id == -1) {
			throw new IllegalArgumentException("No packet id for: " + packet.getClass().getName());
		}

		return id;
	}

	public Packet fromId(int id) {
		PacketEntry<?> entry = this.packetMapping.get(id);
		if (entry == null) {
			return null;
		}

		return entry.supplier.get();
	}

	@Data
	private static class PacketEntry<T extends Packet> {
		private final Class<T> type;
		private final Supplier<T> supplier;
	}
}
