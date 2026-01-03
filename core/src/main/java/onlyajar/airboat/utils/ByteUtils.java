package onlyajar.airboat.utils;

public final class ByteUtils {

    public static String toHex(byte[] data){
        if(data == null) return "";
        StringBuilder buffer = new StringBuilder(data.length << 2);
        for (byte b: data) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                buffer.append('0');
            }
            buffer.append(hex);
        }
        return buffer.toString().toUpperCase();
    }

    public static byte[] fromHex(String hex){
        if (StringUtils.isEmpty(hex)) {
            return new byte[0];
        } else {
            if (hex.length() % 2 != 0) {
                hex = "0" + hex;
            }
            int len = hex.length() >> 1;
            byte[] result = new byte[len];
            for (int i=0; i< hex.length(); i++) {
                int shift = i%2 == 1 ? 0 : 4;
                result[i>>1] |= (byte) (Character.digit(hex.charAt(i), 16) << shift);
            }
            return result;
        }
    }

    public static byte[] concat(byte[] array1, byte[] array2) {
        byte[] concatArray = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, concatArray, 0, array1.length);
        System.arraycopy(array2, 0, concatArray, array1.length, array2.length);
        return  concatArray;
    }

    public static byte[] sub(byte[] array, int beginIndex){
        int len = array.length - beginIndex;
        byte[] subArray = new byte[len];
        System.arraycopy(array, beginIndex, subArray, 0, len);
        return  subArray;
    }

    public static byte[] sub(byte[] array, int beginIndex, int endIndex){
        int len = endIndex - beginIndex;
        byte[] subArray = new byte[len];
        System.arraycopy(array, beginIndex, subArray, 0, len);
        return  subArray;
    }

    public static int toInt(byte[] bytes) {
        int temp = 0;
        for(int i = 0; i < bytes.length; ++i) {
            temp += (bytes[i] & 255) << 8 * (bytes.length - 1 - i);
        }
        return temp;
    }

    public static byte[] intToByteArray(int data, int len) {
        byte[] result = new byte[len];
        for(int i = 0; i < 4 && i < len; ++i) {
            result[len - 1 - i] = (byte) (data >> 8 * i & 0x000000FF);
        }
        return result;
    }

    public static byte xor(byte byte1, byte byte2){
        return (byte) ((byte1 & 0xFF) ^ (byte2 & 0xFF));
    }

    public static byte[] xor(byte[] bytes1, byte[] bytes2) {
        int length = bytes1.length;
        if (length != bytes2.length) {
            return null;
        }
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = xor(bytes1[i], bytes2[i]);
        }
        return result;
    }
}
