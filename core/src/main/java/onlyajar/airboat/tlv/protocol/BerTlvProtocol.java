package onlyajar.airboat.tlv.protocol;

import java.util.Arrays;

/**
 * ┌──────────┬───────────────────────────────────────────
 * │  字段     │  编码规则
 * ├──────────┼───────────────────────────────────────────
 * │  Tag     │  1字节: b5~b1 ≠ 11111
 * │          │  多字节: 首字节 b5~b1 = 11111,
 * │          │  后续字节 b7=1继续, b7=0结束
 * │          │   b6 = 0 → Primitive, b6 = 1 → Constructed
 * ├──────────┼───────────────────────────────────────────
 * │  Length  │  b7=0: 短格式, 值 = b6~b0   (0~127)
 * │          │  b7=1: 长格式, b6~b0 = 后续字节数
 * │          │   0x81+1字节 → 128~255
 * │          │   0x82+2字节 → 256~65535
 * │          │   0x83+3字节 → 65536~16777215
 * ├──────────┼───────────────────────────────────────────
 * │  Value   │  基本编码 (Primitive): 原始数据
 * │          │  构造编码 (Constructed): 嵌套的 TLV序列
 * ├──────────┼───────────────────────────────────────────
 * │  Tag type│
 * └──────────┴───────────────────────────────────────────
 * 表示一个 BER-TLV 数据对象
 */
public class BerTlvProtocol implements TlvProtocol {

    @Override
    public byte[] paresTag(byte[] data, int offset) {
        if (offset >= data.length) throw new IllegalArgumentException("offset out of bounds");
        // Tag 第一个字节: 低5位全为1 → 后续字节属于 tag
        boolean multiByte = (data[offset] & 0x1F) == 0x1F;
        int end = offset + 1;
        if (multiByte) {
            // 后续字节的 bit7 = 1 表示还有更多 tag 字节
            while (end < data.length && (data[end] & 0x80) != 0) {
                end++;
            }
            if (end < data.length) end++; // 最后一个 tag 字节 (bit7=0)
            else throw new IllegalArgumentException("tag out of bounds at offset at offset " + offset);
        }
        return Arrays.copyOfRange(data, offset, end);
    }

    @Override
    public int[] decodeLength(byte[] data, int offset) {
        if (offset >= data.length) throw new IllegalArgumentException("offset out of bounds for length");

        int first = data[offset] & 0xFF;

        if (first <= 0x7F) {
            return new int[]{first, 1};
        }

        int numLengthBytes = first & 0x7F;
        if (numLengthBytes == 0) {
            throw new UnsupportedOperationException("indefinite length not supported");
        }
        if (offset + 1 + numLengthBytes > data.length) {
            throw new IllegalArgumentException("length field truncated at offset " + offset);
        }

        int length = 0;
        for (int i = 1; i <= numLengthBytes; i++) {
            length = (length << 8) | (data[offset + i] & 0xFF);
        }
        return new int[]{length, 1 + numLengthBytes};
    }

    @Override
    public byte[] encodeLength(int length) {
        if (length < 0) throw new IllegalArgumentException("length must greater than 0, instead of " + length);
        if (length <= 0x7F) {
            return new byte[]{(byte) length};
        } else if (length <= 0xFF) {
            return new byte[]{(byte) 0x81, (byte) length};
        } else if (length <= 0xFFFF) {
            return new byte[]{(byte) 0x82, (byte) (length >> 8), (byte) length};
        } else if (length <= 0xFFFFFF) {
            return new byte[]{(byte) 0x83,
                    (byte) (length >> 16), (byte) (length >> 8), (byte) length};
        } else {
            return new byte[]{(byte) 0x84,
                    (byte) (length >> 24), (byte) (length >> 16), (byte) (length >> 8), (byte) length};
        }
    }
}
