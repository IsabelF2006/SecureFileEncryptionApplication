
package com.isabel.fileencryption;

import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;

public class FileDecryptor {

    private static final int GCM_TAG_LENGTH = 128;
    private static final int SALT_LENGTH = 16;
    private static final int IV_LENGTH = 12;

    public static void decrypt(
            String inputFile,
            String outputFile,
            char[] password) throws Exception {

        Path target = Paths.get(outputFile)
                .toAbsolutePath()
                .normalize();

        Path parent = target.getParent();

        if (Files.exists(target)) {
            throw new FileAlreadyExistsException(
                    "The decrypted output already exists: " + target
            );
        }

        Path temporaryFile = null;
        boolean completed = false;

        try (DataInputStream input = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(inputFile)))) {

            byte[] salt = new byte[SALT_LENGTH];
            byte[] iv = new byte[IV_LENGTH];

            try {
                input.readFully(salt);
                input.readFully(iv);
            } catch (EOFException e) {
                throw new Exception("Invalid encrypted file.", e);
            }

            SecretKey key = CryptoUtils.generateKey(password, salt);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            GCMParameterSpec gcmSpec =
                    new GCMParameterSpec(GCM_TAG_LENGTH, iv);

            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);

            // Store decrypted bytes in a temporary file first.
            temporaryFile = Files.createTempFile(
                    parent, ".decrypt-", ".tmp"
            );

            try (OutputStream output = new BufferedOutputStream(
                    Files.newOutputStream(temporaryFile))) {

                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = input.read(buffer)) != -1) {
                    byte[] decryptedBytes =
                            cipher.update(buffer, 0, bytesRead);

                    if (decryptedBytes != null) {
                        output.write(decryptedBytes);
                    }
                }

                // Authentication must succeed before finalizing the file.
                try {
                    byte[] finalBytes = cipher.doFinal();

                    if (finalBytes != null) {
                        output.write(finalBytes);
                    }

                } catch (AEADBadTagException e) {
                    throw new Exception(
                            "Decryption failed. Incorrect password " +
                                    "or corrupted file.",
                            e
                    );
                } catch (GeneralSecurityException e) {
                    throw new Exception(
                            "Decryption failed. Incorrect password " +
                                    "or corrupted file.",
                            e
                    );
                }
            }

            // Publish the decrypted file only after successful authentication.
            // No REPLACE_EXISTING option is used.
            Files.move(temporaryFile, target);
            completed = true;

            System.out.println("File decrypted successfully.");

        } finally {
            // Remove temporary plaintext if anything fails.
            if (!completed && temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (Exception cleanupError) {
                    System.err.println(
                            "Warning: Could not remove temporary file: " +
                                    temporaryFile
                    );
                }
            }
        }
    }
}
