package dev.thomazz.litelimbo.packet.impl.game;

import dev.thomazz.litelimbo.packet.Packet;
import dev.thomazz.litelimbo.packet.property.Version;
import dev.thomazz.litelimbo.packet.property.Direction;
import dev.thomazz.litelimbo.util.MinecraftBufferReader;
import io.netty.buffer.ByteBuf;
import java.util.EnumSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Position implements Packet {
    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround; // 1.7
    private Set<EnumFlags> flags; // 1.8+
    private int teleportId; // 1.9+

    @Override
    public void write(ByteBuf buf, Version version, Direction direction) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);

        buf.writeFloat(this.yaw);
        buf.writeFloat(this.pitch);

        if (version.compareTo(Version.MINECRAFT_1_8) >= 0) {
            buf.writeByte(EnumFlags.combine(this.flags));
        } else {
            buf.writeBoolean(this.onGround);
        }

        if (version.compareTo(Version.MINECRAFT_1_9) >= 0) {
            MinecraftBufferReader.writeVarInt(buf, this.teleportId);
        }
    }

    public enum EnumFlags {
        X(0),
        Y(1),
        Z(2),
        Y_ROT(3),
        X_ROT(4);

        private int id;

        EnumFlags(int id) {
            this.id = id;
        }

        private int shiftId() {
            return 1 << this.id;
        }

        private boolean compareCombined(int combine) {
            return (combine & this.shiftId()) == this.shiftId();
        }

        public static Set<EnumFlags> getFlags(int combinedId) {
            Set<EnumFlags> set = EnumSet.noneOf(EnumFlags.class);

            for (EnumFlags flags : values()) {
                if (flags.compareCombined(combinedId)) {
                    set.add(flags);
                }
            }

            return set;
        }

        public static int combine(Set<EnumFlags> flags) {
            int i = 0;

            for (EnumFlags flag : flags) {
                i |= flag.shiftId();
            }

            return i;
        }
    }
}
