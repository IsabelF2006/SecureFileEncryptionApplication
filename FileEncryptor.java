package com.isabel.fileencryption;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;

public class FileEncryptor {

    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12;
    private static final int SALT_LENGTH = 16;
    private static final int BUFFER_SIZE = 4096;

    public static void encrypt(
            String inputFile,
            String outputFile,
            char[] password) throws Exception {

        Path source = Paths.get(inputFile)
                .toAbsolutePath().normalize();

        Path target = Paths.get(outputFile)
                .toAbsolutePath().normalize();

        if (!Files.isRegularFile(source)
                || !Files.isReadable(source)) {
            throw new IllegalArgumentException(
                    "The input file does not exist or cannot be read.");
        }

        if (source.equals(target)) {
            throw new IllegalArgumentException(
                    "Input and output files must be different.");
        }

        if (Files.exists(target)) {
            throw new FileAlreadyExistsException(
                    "The encrypted output already exists: " + target);
        }

        Path parent = target.getParent();

        if (parent == null || !Files.isDirectory(parent)) {
            throw new IllegalArgumentException(
                    "The output directory does not exist.");
        }

        Path temporaryFile = null;
        boolean completed = false;

        try {
            byte[] salt = CryptoUtils.generateSalt();
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            SecretKey key = CryptoUtils.generateKey(password, salt);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    key,
                    new GCMParameterSpec(GCM_TAG_LENGTH, iv)
            );

            temporaryFile = Files.createTempFile(
                    parent, ".encrypt-", ".tmp");

            try (InputStream input = new BufferedInputStream(
                    Files.newInputStream(source));
                 OutputStream output = new BufferedOutputStream(
                         Files.newOutputStream(temporaryFile))) {

                // File format: 16-byte salt, 12-byte IV,
                // followed by ciphertext and the GCM authentication tag.
                output.write(salt);
                output.write(iv);

                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;

                while ((bytesRead = input.read(buffer)) != -1) {
                    byte[] encrypted = cipher.update(
                            buffer, 0, bytesRead);

                    if (encrypted != null && encrypted.length > 0) {
                        output.write(encrypted);
                    }
                }

                byte[] finalBytes = cipher.doFinal();
                output.write(finalBytes);
            }

            // Publish the encrypted file only after successful completion.
            // Do not overwrite an existing output file.
            Files.move(temporaryFile, target);
            completed = true;

            System.out.println("File encrypted successfully.");

        } finally {
            if (!completed && temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (Exception cleanupError) {
                    System.err.println(
                            "Warning: Could not remove temporary file: "
                                    + temporaryFile);
                }
            }
        }
    }
}