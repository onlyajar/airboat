package onlyajar.airboat.tlv;


import java.util.ArrayList;
import java.util.List;

import onlyajar.airboat.tlv.protocol.BerTlvProtocol;
import onlyajar.airboat.tlv.protocol.TlvProtocol;


public class TlvBuilder {

    private final List<TlvData> tlvList = new ArrayList<>();

    private final TlvProtocol tlvProtocol;

    public TlvBuilder() {
        tlvProtocol = new BerTlvProtocol();

    }

    public TlvBuilder(TlvProtocol tlvProtocol) {
        this.tlvProtocol = tlvProtocol;
    }

    public TlvBuilder addPrimitive(String tagHex, String valueHex) {
        return add(new TlvData(
                HexUtils.hexToBytes(tagHex),
                HexUtils.hexToBytes(valueHex)
        ));
    }

    public TlvBuilder addPrimitive(String tagHex, byte[] value) {
        return add(new TlvData(
                HexUtils.hexToBytes(tagHex),
                value
        ));
    }

    public TlvBuilder addPrimitive(byte[] tag, String valueHex) {
        return add(new TlvData(tag, HexUtils.hexToBytes(valueHex)));
    }

    public TlvBuilder addPrimitive(byte[] tag, byte[] value) {
        return add(new TlvData(tag, value));
    }


    public TlvBuilder addConstructed(String tagHex, BuilderCallback callback) {
        TlvBuilder childBuilder = new TlvBuilder();
        callback.build(childBuilder);
        return  add(new TlvData(
                HexUtils.hexToBytes(tagHex),
                childBuilder.tlvList
        ));
    }

    public TlvBuilder addConstructed(byte[] tag, BuilderCallback callback) {
        TlvBuilder childBuilder = new TlvBuilder();
        callback.build(childBuilder);
        return add(new TlvData(
                tag,
                childBuilder.tlvList
        ));
    }

    public TlvBuilder add(TlvData tlv) {
        boolean isTag = tlvProtocol.isValidTag(tlv.getTag());
        if(isTag){
            tlvList.add(tlv);
        }
        return this;
    }

    @FunctionalInterface
    public interface BuilderCallback {
        void build(TlvBuilder builder);
    }

    public byte[] build() {
        List<byte[]> parts = new ArrayList<>();
        int totalLen = 0;

        for (TlvData tlv : tlvList) {
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
            TlvBuilder childBuilder = new TlvBuilder();
            for (TlvData child : tlv.getChildren()) {
                childBuilder.add(child);
            }
            value = childBuilder.build();
        } else {
            value = tlv.getValue() != null ? tlv.getValue() : new byte[0];
        }

        byte[] lengthBytes = tlvProtocol.encodeLength(value.length);

        // tag + length + value
        byte[] result = new byte[tag.length + lengthBytes.length + value.length];
        System.arraycopy(tag, 0, result, 0, tag.length);
        System.arraycopy(lengthBytes, 0, result, tag.length, lengthBytes.length);
        System.arraycopy(value, 0, result, tag.length + lengthBytes.length, value.length);

        return result;
    }

}

