package onlyajar.airboat.base;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class Charsets {
    private Charsets() {
        throw new AssertionError("No Charsets instances for you!");
    }

    public static final Charset ISO_8859_1 = StandardCharsets.ISO_8859_1;

    public static final Charset UTF_8 = StandardCharsets.UTF_8;

    public static final Charset GBK = Charset.forName("GBK");
}
