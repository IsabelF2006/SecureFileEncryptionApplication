
package com.isabel.fileencryption;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;

public class EncryptionGUI extends JFrame {

    private JTextField fileField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel statusLabel;

    public EncryptionGUI() {
        setTitle("Secure File Encryption");
        setSize(750, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createInterface();
        setVisible(true);
    }

    private void createInterface() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Selected File:"), gbc);

        fileField = new JTextField();
        fileField.setEditable(false);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(fileField, gbc);

        JButton browseButton = new JButton("Browse");
        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(browseButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField();
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Confirm Password:"), gbc);

        confirmPasswordField = new JPasswordField();
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(confirmPasswordField, gbc);

        JButton encryptButton = new JButton("Encrypt File");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        panel.add(encryptButton, gbc);

        JButton decryptButton = new JButton("Decrypt File");
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(decryptButton, gbc);

        statusLabel = new JLabel("Status: Ready");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        panel.add(statusLabel, gbc);

        add(panel);

        browseButton.addActionListener(e -> selectFile());
        encryptButton.addActionListener(e -> encryptSelectedFile());
        decryptButton.addActionListener(e -> decryptSelectedFile());
    }

    private void selectFile() {
        JFileChooser chooser = new JFileChooser();

        if (chooser.showOpenDialog(this)
                == JFileChooser.APPROVE_OPTION) {
            fileField.setText(
                    chooser.getSelectedFile().getAbsolutePath()
            );
            statusLabel.setText("Status: File selected.");
        }
    }

    private void encryptSelectedFile() {
        char[] password = passwordField.getPassword();
        char[] confirmation = confirmPasswordField.getPassword();

        try {
            String inputFile = fileField.getText();

            if (inputFile.isEmpty()) {
                showError("Please select a file first.");
                return;
            }

            File source = new File(inputFile);

            if (!source.isFile() || !source.canRead()) {
                showError("The selected file does not exist or cannot be read.");
                return;
            }

            if (inputFile.endsWith(".enc")) {
                showError("Select the original file, not an .enc file.");
                return;
            }

            if (!isStrongPassword(password)) {
                showError(
                        "Use a password with at least 12 characters, " +
                                "including uppercase and lowercase letters, " +
                                "a number, and a special character."
                );
                return;
            }

            if (!Arrays.equals(password, confirmation)) {
                showError("The passwords do not match.");
                return;
            }

            String outputFile = inputFile + ".enc";

            if (new File(outputFile).exists()) {
                showError(
                        "The encrypted output already exists. " +
                                "Rename or move it before encrypting again."
                );
                return;
            }

            FileEncryptor.encrypt(inputFile, outputFile, password);

            statusLabel.setText("Status: Encryption successful.");

            JOptionPane.showMessageDialog(
                    this,
                    "File encrypted successfully!\n\n" +
                            "Encrypted file:\n" + outputFile
            );

        } catch (Exception ex) {
            statusLabel.setText("Status: Encryption failed.");
            showError("Encryption failed: " + ex.getMessage());

        } finally {
            Arrays.fill(password, '\0');
            Arrays.fill(confirmation, '\0');
            passwordField.setText("");
            confirmPasswordField.setText("");
        }
    }

    private void decryptSelectedFile() {
        char[] password = passwordField.getPassword();

        try {
            String inputFile = fileField.getText();

            if (inputFile.isEmpty()) {
                showError("Please select an encrypted file first.");
                return;
            }

            File source = new File(inputFile);

            if (!source.isFile() || !source.canRead()) {
                showError("The selected file does not exist or cannot be read.");
                return;
            }

            if (!inputFile.endsWith(".enc")) {
                showError("Please select a .enc encrypted file.");
                return;
            }

            if (password.length == 0) {
                showError("Please enter the original encryption password.");
                return;
            }

            String originalPath = inputFile.substring(
                    0, inputFile.length() - 4
            );

            File originalFile = new File(originalPath);
            String fileName = originalFile.getName();
            int extensionIndex = fileName.lastIndexOf(".");

            String decryptedName;

            if (extensionIndex > 0) {
                decryptedName =
                        fileName.substring(0, extensionIndex) +
                                "_decrypted" +
                                fileName.substring(extensionIndex);
            } else {
                decryptedName = fileName + "_decrypted";
            }

            File parentFolder = originalFile.getParentFile();
            File outputFile = new File(parentFolder, decryptedName);

            if (outputFile.exists()) {
                showError(
                        "The decrypted output already exists. " +
                                "Rename or move it before decrypting again."
                );
                return;
            }

            FileDecryptor.decrypt(
                    inputFile,
                    outputFile.getAbsolutePath(),
                    password
            );

            statusLabel.setText("Status: Decryption successful.");

            JOptionPane.showMessageDialog(
                    this,
                    "File decrypted successfully!\n\n" +
                            "Decrypted file:\n" + outputFile.getAbsolutePath()
            );

        } catch (Exception ex) {
            statusLabel.setText("Status: Decryption failed.");
            showError(
                    ex.getMessage() == null
                            ? "An unexpected error occurred."
                            : ex.getMessage()
            );

        } finally {
            Arrays.fill(password, '\0');
            passwordField.setText("");
            confirmPasswordField.setText("");
        }
    }

    private boolean isStrongPassword(char[] password) {
        if (password.length < 12) {
            return false;
        }

        boolean upper = false;
        boolean lower = false;
        boolean digit = false;
        boolean special = false;

        for (char c : password) {
            if (Character.isUpperCase(c)) upper = true;
            else if (Character.isLowerCase(c)) lower = true;
            else if (Character.isDigit(c)) digit = true;
            else special = true;
        }

        return upper && lower && digit && special;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Notice",
                JOptionPane.WARNING_MESSAGE
        );
    }
}
