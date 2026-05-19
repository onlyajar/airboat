package onlyajar.airboat.tlv;

import java.util.Arrays;

/**
 * 长度编解码 + 通用工具
 */
public final class BerTlvUtil {

    private BerTlvUtil() {}

    // ======================== Length 编码 ========================

    /**
     * 将长度值编码为 BER 长度字节数组
     *
     * BER 长度规则:
     *   - 短格式: 0x00 ~ 0x7F → 1 字节
     *   - 长格式: 0x81 + 1字节  (128~255)
     *             0x82 + 2字节  (256~65535)
     *             0x83 + 3字节  (65536~16777215)
     */
    public static byte[] encodeLengthBytes(int length) {
        if (length < 0) throw new IllegalArgumentException("Length cannot be negative: " + length);

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
            throw new IllegalArgumentException("Length too large: " + length);
        }
    }

    // ======================== Tag 解析 ========================

    /**
     * 从 offset 开始读取一个完整的 Tag
     * @return tag 字节数组
     */
    public static byte[] parseTag(byte[] data, int offset) {
        if (offset >= data.length) throw new IllegalArgumentException("Offset out of bounds");

        // Tag 第一个字节: 低5位全为1 → 后续字节属于 tag
        boolean multiByte = (data[offset] & 0x1F) == 0x1F;

        int end = offset + 1;
        if (multiByte) {
            // 后续字节的 bit7 = 1 表示还有更多 tag 字节
            while (end < data.length && (data[end] & 0x80) != 0) {
                end++;
                if (end - offset > 4) {
                    throw new IllegalArgumentException("Tag too long at offset " + offset);
                }
            }
            if (end < data.length) end++; // 最后一个 tag 字节 (bit7=0)
            else throw new IllegalArgumentException("Truncated tag at offset " + offset);
        }

        return Arrays.copyOfRange(data, offset, end);
    }

    /**
     * 从 offset 开始读取 Length，返回 [lengthValue, lengthFieldSize]
     */
    public static int[] parseLength(byte[] data, int offset) {
        if (offset >= data.length) throw new IllegalArgumentException("Offset out of bounds for length");

        int first = data[offset] & 0xFF;

        if (first <= 0x7F) {
            // 短格式
            return new int[]{first, 1};
        }

        int numLengthBytes = first & 0x7F;
        if (numLengthBytes == 0) {
            throw new UnsupportedOperationException("Indefinite length not supported");
        }
        if (offset + 1 + numLengthBytes > data.length) {
            throw new IllegalArgumentException("Length field truncated at offset " + offset);
        }

        int length = 0;
        for (int i = 1; i <= numLengthBytes; i++) {
            length = (length << 8) | (data[offset + i] & 0xFF);
        }

        return new int[]{length, 1 + numLengthBytes};
    }
}
