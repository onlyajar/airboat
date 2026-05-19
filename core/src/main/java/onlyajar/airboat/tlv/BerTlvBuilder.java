package onlyajar.airboat.tlv;

import java.util.*;

/**
 * BER-TLV 组包器
 *
 * 用法:
 *   byte[] result = new BerTlvBuilder()
 *       .addPrimitive("9F26", "AABBCCDDAABBCCDD")
 *       .addPrimitive("9F27", "80")
 *       .addPrimitive("9F10", "0102030405")
 *       .addConstructed("70", builder ->
 *           builder.addPrimitive("57", "1234567890123456D2512201000000000F")
 *                  .addPrimitive("5A", "1234567890123456")
 *       )
 *       .build();
 */
public class BerTlvBuilder {

    private final List<TlvData> tlvs = new ArrayList<>();

    public BerTlvBuilder() {}

    // ======================== 添加基本编码节点 ========================

    /** 添加一个基本 (Primitive) 节点，tag 和 value 均为十六进制字符串 */
    public BerTlvBuilder addPrimitive(String tagHex, String valueHex) {
        tlvs.add(new TlvData(
                BerTlvParser.hexToBytes(tagHex),
                BerTlvParser.hexToBytes(valueHex)
        ));
        return this;
    }

    /** 添加一个基本节点，tag 和 value 为原始字节 */
    public BerTlvBuilder addPrimitive(byte[] tag, byte[] value) {
        tlvs.add(new TlvData(tag, value));
        return this;
    }

    /** 添加原始 TlvData 对象 */
    public BerTlvBuilder add(TlvData tlv) {
        tlvs.add(tlv);
        return this;
    }

    // ======================== 添加构造节点（Template） ========================

    /** 添加一个构造 (Constructed) 节点，通过回调构建子节点 */
    public BerTlvBuilder addConstructed(String tagHex, BuilderCallback callback) {
        BerTlvBuilder childBuilder = new BerTlvBuilder();
        callback.build(childBuilder);
        tlvs.add(new TlvData(
                BerTlvParser.hexToBytes(tagHex),
                childBuilder.tlvs
        ));
        return this;
    }

    /** 函数式接口：用于嵌套构建子节点 */
    @FunctionalInterface
    public interface BuilderCallback {
        void build(BerTlvBuilder builder);
    }

    // ======================== 编码输出 ========================

    /** 编码为字节数组 */
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

    /** 编码为十六进制字符串 */
    public String buildHex() {
        return TlvData.bytesToHex(build());
    }

    // ======================== 内部编码 ========================

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

    // ======================== 静态快捷方法 ========================

    /** 直接从单个 tag-value 对快速编码 */
    public static byte[] encodeSingle(String tagHex, String valueHex) {
        return new BerTlvBuilder().addPrimitive(tagHex, valueHex).build();
    }
}

