package onlyajar.airboat.tlv;
import java.util.*;

/**
 * BER-TLV 解析器
 *
 * 支持:
 *   - 基本编码 (Primitive)
 *   - 构造编码 (Constructed / Template，递归解析子节点)
 *   - 多字节 Tag
 *   - 多字节 Length
 */
public class BerTlvParser {

    private BerTlvParser() {}

    /**
     * 解析一整块 BER-TLV 数据，返回顶层 TLV 列表
     */
    public static List<TlvData> parse(byte[] data) {
        Objects.requireNonNull(data, "Data cannot be null");
        List<TlvData> result = new ArrayList<>();
        int offset = 0;

        while (offset < data.length) {
            // 1. 解析 Tag
            byte[] tag = BerTlvUtil.parseTag(data, offset);
            offset += tag.length;

            // 2. 解析 Length
            int[] lenInfo = BerTlvUtil.parseLength(data, offset);
            int valueLength = lenInfo[0];
            int lengthFieldSize = lenInfo[1];
            offset += lengthFieldSize;

            // 3. 提取 Value
            if (offset + valueLength > data.length) {
                throw new IllegalArgumentException(String.format(
                        "Value truncated: tag=%s, expectedLen=%d, available=%d",
                        TlvData.bytesToHex(tag), valueLength, data.length - offset));
            }
            byte[] value = Arrays.copyOfRange(data, offset, offset + valueLength);
            offset += valueLength;

            // 4. 如果是构造类型，递归解析子节点
            boolean isConstructed = (tag[0] & 0x20) != 0;
            if (isConstructed && valueLength > 0) {
                List<TlvData> children = parse(value);
                result.add(new TlvData(tag, children));
            } else {
                result.add(new TlvData(tag, value));
            }
        }

        return result;
    }

    /**
     * 在整个数据中按标签查找（扁平化搜索）
     */
    public static TlvData findByTag(byte[] data, byte[] searchTag) {
        List<TlvData> tlvs = parse(data);
        for (TlvData tlv : tlvs) {
            if (Arrays.equals(tlv.getTag(), searchTag)) return tlv;
            TlvData found = tlv.find(searchTag);
            if (found != null) return found;
        }
        return null;
    }

    /**
     * 辅助：按十六进制标签字符串查找
     */
    public static TlvData findByTagHex(byte[] data, String tagHex) {
        return findByTag(data, hexToBytes(tagHex));
    }

    // ======================== 反向查找（从末尾扫描，处理重复标签） ========================

    /**
     * 在扁平列表中查找所有匹配的 TLV
     */
    public static List<TlvData> findAllByTag(byte[] data, byte[] searchTag) {
        List<TlvData> result = new ArrayList<>();
        List<TlvData> tlvs = parse(data);
        collectByTag(tlvs, searchTag, result);
        return result;
    }

    private static void collectByTag(List<TlvData> list, byte[] searchTag, List<TlvData> result) {
        for (TlvData tlv : list) {
            if (Arrays.equals(tlv.getTag(), searchTag)) {
                result.add(tlv);
            }
            if (!tlv.getChildren().isEmpty()) {
                collectByTag(tlv.getChildren(), searchTag, result);
            }
        }
    }

    static byte[] hexToBytes(String hex) {
        hex = hex.replaceAll("\\s+", "");
        if (hex.length() % 2 != 0) throw new IllegalArgumentException("Odd-length hex string");
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return bytes;
    }
}

