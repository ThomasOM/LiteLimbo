package dev.thomazz.litelimbo.util;

import lombok.experimental.UtilityClass;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;

@UtilityClass
public class ColorConverter {
	public String convert(String message) {
		return LegacyComponentSerializer.legacy().serialize(
				LegacyComponentSerializer.legacy().deserialize(message, '&')
		);
	}
}
