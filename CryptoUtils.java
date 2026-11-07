
package com.isabel.fileencryption;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

public class CryptoUtils {

    private static final int AES_KEY_SIZE = 256;
    private static final int ITERATIONS = 600_000;
    private static final int SALT_LENGTH = 16;

    /**
     * Derives a 256-bit AES key from a password and random salt.
     */
    public static SecretKey generateKey(
            char[] password,
            byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        PBEKeySpec spec = new PBEKeySpec(
                password,
                salt,
                ITERATIONS,
                AES_KEY_SIZE
        );

        byte[] keyBytes = null;

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256"
                    );

            keyBytes = factory.generateSecret(spec).getEncoded();

            // SecretKeySpec copies the supplied key bytes.
            return new SecretKeySpec(keyBytes, "AES");

        } finally {
            spec.clearPassword();

            if (keyBytes != null) {
                Arrays.fill(keyBytes, (byte) 0);
            }
        }
    }

    /**
     * Generates a cryptographically random salt.
     */
    public static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return salt;
    }
}
