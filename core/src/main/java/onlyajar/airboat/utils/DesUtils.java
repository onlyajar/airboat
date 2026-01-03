package onlyajar.airboat.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
public class DesUtils {
    private final static String DES = "DES";
    private final static String CIPHER_ALGORITHM = "DES/ECB/NoPadding";

    public static byte[] encrypt(byte[] src, byte[] key) {
        SecureRandom sr = new SecureRandom();
        try {
            DESKeySpec dks = new DESKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
            SecretKey secureKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secureKey, sr);
            return cipher.doFinal(src);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return null;
    }

    public static byte[] generatorKey() throws NoSuchAlgorithmException {
        KeyGenerator kg = KeyGenerator.getInstance(DES);
        kg.init(16);
        SecretKey secretKey = kg.generateKey();
        return secretKey.getEncoded();
    }

    public static byte[] decrypt(byte[] src, byte[] key) {
        SecureRandom sr = new SecureRandom();
        try {
            DESKeySpec dks = new DESKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
            SecretKey secureKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secureKey, sr);
            return cipher.doFinal(src);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return null;
    }

}
