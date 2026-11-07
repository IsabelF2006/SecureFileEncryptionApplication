# Secure File Encryption Application

**Author:** Isabel Felicioni  
**Project Topic:** Cryptography / Data Security  
**Programming Language:** Java  
**Development Environment:** IntelliJ IDEA

## 1. Project Overview

The Secure File Encryption Application is a Java-based desktop application designed to protect sensitive files from unauthorized access.

The application allows users to select a file, enter a password, encrypt the file, and later decrypt it using the correct password. It uses modern cryptographic techniques to help protect the confidentiality and integrity of stored information.

This project was developed as an Individual Security Project to demonstrate how cryptography can be implemented in a practical software application.

## 2. Security Features

The application includes the following security features:

- **AES-256-GCM Encryption:** Uses Advanced Encryption Standard with a 256-bit key to encrypt files and provide authentication and integrity protection.
- **PBKDF2 with HMAC-SHA256:** Derives a secure encryption key from the user's password using 600,000 iterations.
- **Random Salt Generation:** Generates a random 16-byte salt during encryption.
- **Initialization Vector (IV):** Generates a random 12-byte IV for AES-GCM encryption.
- **Password Requirements:** Requires passwords to contain at least 12 characters, including an uppercase letter, lowercase letter, number, and special character.
- **Duplicate Encryption Prevention:** Prevents unintended duplicate encryption operations.
- **Secure Decryption:** Uses temporary files and only saves decrypted output after successful authentication.
- **Error Handling:** Detects unsuccessful decryption attempts caused by incorrect passwords or corrupted encrypted files.
- **Sensitive Data Handling:** Clears password-related data and derived key bytes from memory where possible.

## 3. Technologies Used

- Java
- JDK 21
- Java Swing (Graphical User Interface)
- Java Cryptography Architecture (JCA)
- IntelliJ IDEA

The application uses Java's built-in cryptographic libraries to perform encryption, decryption, and password-based key derivation.

## 4. Project Structure

The application is organized into the following Java classes:

| File | Description |
|---|---|
| `Main.java` | Starts the application and launches the GUI. |
| `EncryptionGUI.java` | Provides the graphical user interface and handles user interactions. |
| `FileEncryptor.java` | Handles file encryption and creates `.enc` files. |
| `FileDecryptor.java` | Handles file decryption and authentication checks. |
| `CryptoUtils.java` | Contains cryptographic utilities and password-based key derivation. |

## 5. Requirements

To run the application, you will need:

- Java Development Kit (JDK) 21 or a compatible version.
- IntelliJ IDEA or another Java IDE.
- The project's Java source files.

No additional external libraries are required for the cryptographic functionality because the application uses Java's built-in security libraries.

## 6. How to Run the Application

1. Clone or download this GitHub repository.
2. Open IntelliJ IDEA.
3. Select **Open** and choose the downloaded project folder.
4. Allow IntelliJ IDEA to configure the Java project.
5. Make sure JDK 21 is selected as the project SDK.
6. Locate `Main.java` in the project.
7. Right-click `Main.java` and select **Run 'Main.main()'**.
8. The Secure File Encryption Application graphical interface should open.

## 7. How to Use the Application

### Encrypting a File

1. Open the application.
2. Click the Browse button to select a file.
3. Enter a password that meets the application's requirements.
4. Confirm the password if prompted.
5. Select the encryption option.
6. The application creates an encrypted file with a `.enc` extension.

### Decrypting a File

1. Select the previously encrypted `.enc` file.
2. Enter the same password used during encryption.
3. Select the decryption option.
4. If authentication succeeds, the application restores the original file contents.

If an incorrect password is entered or the encrypted file is corrupted, the application displays an error message and does not save unauthenticated decrypted output.

## 8. Testing

The application was tested using a sample text file named `test.txt`.

Testing included:

- Verifying password requirements.
- Successfully encrypting a sample file.
- Confirming that an encrypted `.enc` file was generated.
- Testing duplicate encryption prevention.
- Successfully decrypting the encrypted file using the correct password.
- Comparing the decrypted contents with the original file.
- Testing unsuccessful decryption with an incorrect password.
- Reviewing temporary file handling and protection against accidental overwriting.

These tests demonstrated that the application's primary encryption and decryption functions worked as intended.

## 9. Limitations and Future Improvements

Potential future improvements include:

- Adding additional password-strength feedback.
- Expanding testing with larger files and additional file formats.
- Improving the graphical interface.
- Adding additional security testing and validation.
- Expanding application logging without exposing sensitive information.

The security of the application also depends on users choosing strong passwords and keeping them private. If a password is lost, the encrypted file cannot be decrypted without the correct password.

## 10. Author

**Isabel Felicioni**

Individual Security Project  
Security Topic: Cryptography / Data Security

GitHub Repository:  
https://github.com/IsabelF2006/SecureFileEncryptionApplication
