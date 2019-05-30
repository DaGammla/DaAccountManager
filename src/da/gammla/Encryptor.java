package da.gammla;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;

public class Encryptor {




    private static byte[] fileProcessor(int cipherMode,String key,File inputFile) throws Exception{

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(key.getBytes(StandardCharsets.UTF_8));

        Key secretKey = new SecretKeySpec(hash, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(cipherMode, secretKey);

        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) inputFile.length()];
        inputStream.read(inputBytes);
        inputStream.close();

        byte[] outputBytes = cipher.doFinal(inputBytes);

        return outputBytes;
    }


    /**Encrypts one file to a second file using a key derived from a passphrase:**/
    public static void encryptFile(String fileName, String targetFileName, String key) throws Exception {

        byte[] encData = fileProcessor(Cipher.ENCRYPT_MODE, key, new File(fileName));

        //Write the encrypted data to a new file:
        FileOutputStream outStream = new FileOutputStream(new File(targetFileName));
        outStream.write(encData);
        outStream.close();
    }


    /**Decrypts one file to a second file using a key derived from a passphrase:**/
    public static byte[] decryptFile(String fileName, String key) throws Exception{
        return fileProcessor(Cipher.DECRYPT_MODE, key, new File(fileName));
    }
}
