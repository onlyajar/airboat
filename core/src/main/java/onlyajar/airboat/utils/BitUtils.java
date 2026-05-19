package onlyajar.airboat.utils;

public class BitUtils {
    // 判断第 n 位是否为 1（n 从 0 开始，最低位为第 0 位）
    public static boolean isBitSet(byte b, int n) {
        return (b & (1 << n)) != 0;
    }
}
