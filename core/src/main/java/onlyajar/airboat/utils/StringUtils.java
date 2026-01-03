package onlyajar.airboat.utils;

public final class StringUtils {
    public static boolean isEmpty(String s){
        return s == null || s.isEmpty();
    }

    public static boolean isBlank(String s){
        return s.trim().isEmpty();
    }

    public static boolean isZero( String s ) {
        int i = 0, len = s.length();
        while ( i < len && s.charAt( i ) == '0'){
            i++;
        }
        return i >= len;
    }
}
