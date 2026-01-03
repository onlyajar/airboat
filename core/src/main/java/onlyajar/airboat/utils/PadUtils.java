package onlyajar.airboat.utils;

public class PadUtils {

    public static String zeroPadLeft(String s, int len){
        return padLeft(s, '0', len);
    }

    public static String blankPadLeft(String s, int len){
        return padLeft(s, ' ', len);
    }

    public static String padLeft(String s, char c, int len){
        if(len <= s.length()) return s;
        int padLength = len - s.length();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0 ; i < padLength ; i++) {
            stringBuilder.append(c);
        }
        stringBuilder.append(s);
        return stringBuilder.toString();
    }

    public static String padRight(String s, char c, int len){
        if(len <= s.length()) return s;
        int padLength = len - s.length();
        StringBuilder stringBuilder = new StringBuilder(s);
        for (int i = 0 ; i < padLength ; i++) {
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }
}
