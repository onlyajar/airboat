package onlyajar.airboat.utils;

import java.util.Random;

public final class RandomUtils {
    private static final Random RANDOM = new Random();
    private RandomUtils(){
        throw new UnsupportedOperationException();
    }

    public static boolean nextBoolean() {
        return RANDOM.nextBoolean();
    }

    public static byte[] nextBytes(final int count) {
        final byte[] result = new byte[count];
        RANDOM.nextBytes(result);
        return result;
    }

    public static String nextString(int length){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <length ; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

}
