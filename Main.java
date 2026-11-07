/*
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.SecureRandom;

import java.nio.file.Files;
import java.nio.file.Paths;
 */

package com.isabel.fileencryption;

public class Main {

    public static void main(String[] args) {

        System.out.println("Secure File Encryption Application");

        new EncryptionGUI();
    }
}