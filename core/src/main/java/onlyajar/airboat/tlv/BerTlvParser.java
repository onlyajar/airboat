package onlyajar.airboat.tlv;
import java.util.*;

public class BerTlvParser {

    private BerTlvParser() {}

    public static List<TlvData> parse(byte[] data) {
        Objects.requireNonNull(data, "data cannot be null");
        List<TlvData> result = new ArrayList<>();
        int offset = 0;

        while (offset < data.length) {
            // 1. parse Tag
            byte[] tag = BerTlvUtil.parseTag(data, offset);
            offset += tag.length;

            // 2. parse Length
            int[] lenInfo = BerTlvUtil.parseLength(data, offset);
            int valueLength = lenInfo[0];
            int lengthFieldSize = lenInfo[1];
            offset += lengthFieldSize;

            // 3. get Value
            if (offset + valueLength > data.length) {
                throw new IllegalArgumentException(String.format(
                        "Value truncated: tag=%s, expectedLen=%d, available=%d",
                        TlvData.bytesToHex(tag), valueLength, data.length - offset));
            }
            byte[] value = Arrays.copyOfRange(data, offset, offset + valueLength);
            offset += valueLength;

            // 4. is Constructed
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

    public static TlvData findByTag(byte[] data, byte[] searchTag) {
        List<TlvData> tlvs = parse(data);
        for (TlvData tlv : tlvs) {
            if (Arrays.equals(tlv.getTag(), searchTag)) return tlv;
            TlvData found = tlv.find(searchTag);
            if (found != null) return found;
        }
        return null;
    }

    public static TlvData findByTagHex(byte[] data, String tagHex) {
        return findByTag(data, BerTlvUtil.hexToBytes(tagHex));
    }

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
}

