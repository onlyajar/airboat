package onlyajar.airboat.tlv.protocol;

public interface TlvProtocol {
    byte[] paresTag(byte[] data, int offset);

    boolean isValidTag(byte[] tag);

    int[] decodeLength(byte[] data, int offset);

    byte[] encodeLength(int length);

}
