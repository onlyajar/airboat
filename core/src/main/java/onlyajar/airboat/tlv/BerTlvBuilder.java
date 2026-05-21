package onlyajar.airboat.tlv;

import java.util.*;

/**
 * BER-TLV 组包器
 * <p>
 * byte[] result = new BerTlvBuilder()
 * .addPrimitive("9F26", "AABBCCDDAABBCCDD")
 * .addPrimitive("9F27", "80")
 * .addPrimitive("9F10", "0102030405")
 * .addConstructed("70", builder ->
 * builder.addPrimitive("57", "1234567890123456D2512201000000000F")
 * .addPrimitive("5A", "1234567890123456")
 * )
 * .build();
 */
public class BerTlvBuilder {

    private final List<TlvData> tlvs = new ArrayList<>();

    public BerTlvBuilder() {
    }

    public BerTlvBuilder addPrimitive(String tagHex, String valueHex) {
        tlvs.add(new TlvData(
                BerTlvUtil.hexToBytes(tagHex),
                BerTlvUtil.hexToBytes(valueHex)
        ));
        return this;
    }

    public BerTlvBuilder addPrimitive(String tagHex, byte[] value) {
        tlvs.add(new TlvData(
                BerTlvUtil.hexToBytes(tagHex),
                value
        ));
        return this;
    }

    public BerTlvBuilder addPrimitive(byte[] tag, String valueHex) {
        tlvs.add(new TlvData(tag, BerTlvUtil.hexToBytes(valueHex)));
        return this;
    }

    public BerTlvBuilder addPrimitive(byte[] tag, byte[] value) {
        tlvs.add(new TlvData(tag, value));
        return this;
    }

    public BerTlvBuilder add(TlvData tlv) {
        tlvs.add(tlv);
        return this;
    }

    public BerTlvBuilder addConstructed(String tagHex, BuilderCallback callback) {
        BerTlvBuilder childBuilder = new BerTlvBuilder();
        callback.build(childBuilder);
        tlvs.add(new TlvData(
                BerTlvUtil.hexToBytes(tagHex),
                childBuilder.tlvs
        ));
        return this;
    }

    public BerTlvBuilder addConstructed(byte[] tag, BuilderCallback callback) {
        BerTlvBuilder childBuilder = new BerTlvBuilder();
        callback.build(childBuilder);
        tlvs.add(new TlvData(
                tag,
                childBuilder.tlvs
        ));
        return this;
    }

    @FunctionalInterface
    public interface BuilderCallback {
        void build(BerTlvBuilder builder);
    }

    public byte[] build() {
        List<byte[]> parts = new ArrayList<>();
        int totalLen = 0;

        for (TlvData tlv : tlvs) {
            byte[] encoded = encodeTlv(tlv);
            parts.add(encoded);
            totalLen += encoded.length;
        }

        byte[] result = new byte[totalLen];
        int offset = 0;
        for (byte[] part : parts) {
            System.arraycopy(part, 0, result, offset, part.length);
            offset += part.length;
        }
        return result;
    }

    private byte[] encodeTlv(TlvData tlv) {
        byte[] tag = tlv.getTag();
        byte[] value;

        if (tlv.isConstructed() && tlv.getValue() == null) {
            // 递归编码子节点
            BerTlvBuilder childBuilder = new BerTlvBuilder();
            for (TlvData child : tlv.getChildren()) {
                childBuilder.add(child);
            }
            value = childBuilder.build();
        } else {
            value = tlv.getValue() != null ? tlv.getValue() : new byte[0];
        }

        byte[] lengthBytes = BerTlvUtil.encodeLengthBytes(value.length);

        // tag + length + value
        byte[] result = new byte[tag.length + lengthBytes.length + value.length];
        System.arraycopy(tag, 0, result, 0, tag.length);
        System.arraycopy(lengthBytes, 0, result, tag.length, lengthBytes.length);
        System.arraycopy(value, 0, result, tag.length + lengthBytes.length, value.length);

        return result;
    }

}

