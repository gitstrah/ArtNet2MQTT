package net.ionjet.artnet2mqtt;

import lombok.*;
import org.apache.commons.compress.utils.ByteUtils;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArtNetPacket implements Serializable {

    private int seq;
    private int universe;
    private int length;
    private byte[] data;

    public static final byte[] HEADER = "Art-Net\0".getBytes();
    public static final int PROTOCOL_VERSION = 14;

    public static ArtNetPacket parse(byte[] data, int length) {
        if(data != null && data.length >= 16) {
            if(Arrays.equals(HEADER, Arrays.copyOfRange(data, 0, HEADER.length))) {
                long op = ByteUtils.fromLittleEndian(data, 8, 2);
                long version = ByteBuffer.wrap(Arrays.copyOfRange(data, 10, 12)).getShort();
                if(op == 0x5000 && version == PROTOCOL_VERSION) {
                    int dmxLength = Math.min(getShort(data, 16), length - 18);
                    return new ArtNetPacket().builder()
                            .seq(getByte(data, 12))
                            .universe(getByte(data, 15))
                            .length(dmxLength)
                            .data(Arrays.copyOfRange(data, 18, length))
                            .build();

                }
            }
        }
        return null;
    }

    private static short getShort(byte[] data, int start) {
        return ByteBuffer.wrap(Arrays.copyOfRange(data, start, start + 2)).getShort();
    }

    private static int getByte(byte[] data, int start) {
        return data[start] < 0 ? 256 + data[start] : data[start];
    }


}
