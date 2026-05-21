package onlyajar.airboat.utils;

public class BitUtils {

    public static boolean isBitSet(byte b, int pos) {
        return (b & (1 << pos)) != 0;
    }

    public static byte setBit(byte b, int pos) {
        return (byte) (b | (1 << pos));
    }

}
