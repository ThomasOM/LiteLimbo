package dev.thomazz.litelimbo.packet.property;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Version {
	UNKNOWN(-1),
	MINECRAFT_1_7_2(4),
	MINECRAFT_1_7_6(5),
	MINECRAFT_1_8(47),
	MINECRAFT_1_9(107),
	MINECRAFT_1_9_1(108),
	MINECRAFT_1_9_2(109),
	MINECRAFT_1_9_4(110),
	MINECRAFT_1_10(210),
	MINECRAFT_1_11(315),
	MINECRAFT_1_11_1(316),
	MINECRAFT_1_12(335),
	MINECRAFT_1_12_1(338),
	MINECRAFT_1_12_2(340),
	MINECRAFT_1_13(393),
	MINECRAFT_1_13_1(401),
	MINECRAFT_1_13_2(404),
	MINECRAFT_1_14(477),
	MINECRAFT_1_14_1(480),
	MINECRAFT_1_14_2(485),
	MINECRAFT_1_14_3(490),
	MINECRAFT_1_14_4(498),
	MINECRAFT_1_15(573),
	MINECRAFT_1_15_1(575),
	MINECRAFT_1_15_2(578),
	MINECRAFT_1_16(735),
	MINECRAFT_1_16_1(736),
	MINECRAFT_1_16_2(751),
	MINECRAFT_1_16_3(753),
	MINECRAFT_1_16_4(754),
	MINECRAFT_1_17(755),
	MINECRAFT_1_17_1(756);

	private static final Map<Integer, Version> ID_MAPPING = Version.generateIdMapping();
	private final int protocolId;

	private static Map<Integer, Version> generateIdMapping() {
		ImmutableMap.Builder<Integer, Version> builder = ImmutableMap.builder();
		Arrays.stream(Version.values()).forEach(value -> builder.put(value.getProtocolId(), value));
		return builder.build();
	}

	public static Version fromId(int id) {
		return Version.ID_MAPPING.getOrDefault(id, Version.UNKNOWN);
	}

	public static Version earliest() {
		return Version.MINECRAFT_1_7_2;
	}

	public static Version latest() {
		Version[] values = Version.values();
		return values[values.length - 1];
	}
}
