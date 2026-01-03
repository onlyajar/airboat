package onlyajar.airboat.utils;

public final class ComparableUtils {
    private ComparableUtils(){
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param v1 version
     * @param v2 version
     * @return result
     * result = 0 v1 = v2
     * result > 0 v1 > v2
     * result < 0 v1 < v2
     */

    public static int compareVersion(String v1, String v2){
        String[] sav1 = v1.split("\\.");
        String[] sav2 = v2.split("\\.");
        int times = Math.min(sav1.length, sav2.length);
        int result = 0;
        for (int i = 0; i < times; i++) {
            String s1 = sav1[i];
            String s2 = sav2[i];
            result = s1.compareTo(s2);
            if(result != 0){
                return result;
            }
        }
        return sav1.length - sav2.length;
    }
}
