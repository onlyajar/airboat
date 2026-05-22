package onlyajar.airboat.tlv;

import java.util.Arrays;

public final class HexUtils {

    private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
    private static final int[] HEX_VAL = new int[128];

    static {
        Arrays.fill(HEX_VAL, -1);
        for (int i = '0'; i <= '9'; i++) HEX_VAL[i] = i - '0';
        for (int i = 'a'; i <= 'f'; i++) HEX_VAL[i] = i - 'a' + 10;
        for (int i = 'A'; i <= 'F'; i++) HEX_VAL[i] = i - 'A' + 10;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hex = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hex[i * 2]     = HEX_CHARS[v >>> 4];
            hex[i * 2 + 1] = HEX_CHARS[v & 0x0F];
        }
        return new String(hex);
    }

    public static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            int hi = HEX_VAL[hex.charAt(i)];
            int lo = HEX_VAL[hex.charAt(i + 1)];
            if (hi == -1 || lo == -1) throw new IllegalArgumentException("Invalid hex char");
            bytes[i / 2] = (byte) ((hi << 4) | lo);
        }
        return bytes;
    }
}
