package onlyajar.airboat.tlv;

import java.util.*;

import onlyajar.airboat.tlv.protocol.BerTlvProtocol;
import onlyajar.airboat.tlv.protocol.TlvProtocol;

public final class TlvParser {

    private TlvParser() {
    }

    public static List<TlvData> parse(byte[] data) {
        return parse(data, new BerTlvProtocol());
    }

    public static List<TlvData> parse(byte[] data, TlvProtocol tlvProtocol) {
        Objects.requireNonNull(data, "data cannot be null");
        List<TlvData> result = new ArrayList<>();
        int offset = 0;

        while (offset < data.length) {
            // 1. parse Tag
            byte[] tag = tlvProtocol.paresTag(data, offset);
            offset += tag.length;

            // 2. parse Length
            int[] lenInfo = tlvProtocol.decodeLength(data, offset);
            int valueLength = lenInfo[0];
            int lengthFieldSize = lenInfo[1];
            offset += lengthFieldSize;

            // 3. get Value
            if (offset + valueLength > data.length) {
                throw new IllegalArgumentException(String.format(
                        "Value truncated: tag=%s, expectedLen=%d, available=%d",
                        HexUtils.bytesToHex(tag), valueLength, data.length - offset));
            }
            byte[] value = Arrays.copyOfRange(data, offset, offset + valueLength);
            offset += valueLength;

            // 4. is Constructed
            boolean isConstructed = (tag[0] & 0x20) != 0;
            if (isConstructed && valueLength > 0) {
                List<TlvData> children = parse(value, tlvProtocol);
                result.add(new TlvData(tag, children));
            } else {
                result.add(new TlvData(tag, value));
            }
        }

        return result;
    }

    public static TlvData findByTag(List<TlvData> tlvList, String tagHex) {
        return findByTag(tlvList, HexUtils.hexToBytes(tagHex));
    }

    public static TlvData findByTag(List<TlvData> tlvList, byte[] searchTag) {
        List<TlvData> result = new ArrayList<>();
        collectByTag(tlvList, searchTag, result);
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

    public static List<TlvData> findAllByTag(List<TlvData> tlvList, String tagHex) {
        return findAllByTag(tlvList, HexUtils.hexToBytes(tagHex));
    }

    public static List<TlvData> findAllByTag(List<TlvData> tlvList, byte[] searchTag) {
        List<TlvData> result = new ArrayList<>();
        collectByTag(tlvList, searchTag, result);
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
}

