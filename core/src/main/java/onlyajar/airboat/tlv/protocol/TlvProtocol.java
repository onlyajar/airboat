package onlyajar.airboat.tlv.protocol;

public interface TlvProtocol {
    byte[] paresTag(byte[] data,int offset);

    int[] decodeLength(byte[] data,int offset);

    byte[] encodeLength(int length);

}
