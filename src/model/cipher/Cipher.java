package model.cipher;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.codec.binary.Base64;


public class Cipher
{
    private static final String UNICODE_FORMAT = "UTF8";
    private static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    private javax.crypto.Cipher cipher;
    private SecretKey key;

    public Cipher() throws CipherException
    {
        try
        {
            String myEncryptionKey = "KavrosKavrosKavrosKavros";
            String myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
            byte[] arrayBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
            KeySpec ks = new DESedeKeySpec(arrayBytes);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(myEncryptionScheme);
            cipher = javax.crypto.Cipher.getInstance(myEncryptionScheme);
            key = skf.generateSecret(ks);
        }
        catch (NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException |
                InvalidKeySpecException | UnsupportedEncodingException e)
        {
            throw new CipherException(e.getMessage(),e);
        }
    }
    public String encrypt(String unencryptedString) throws CipherException
    {
        String encryptedString = null;
        try
        {
            cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key);
            byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = cipher.doFinal(plainText);
            encryptedString = new String(Base64.encodeBase64(encryptedText));
        }
        catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e)
        {
            throw new CipherException(e.getMessage(),e);
        }
        return encryptedString;
    }

    public String decrypt(String encryptedString) throws CipherException {
        String decryptedText=null;
        try
        {
            cipher.init(javax.crypto.Cipher.DECRYPT_MODE, key);
            byte[] encryptedText = Base64.decodeBase64(encryptedString);
            byte[] plainText = cipher.doFinal(encryptedText);
            decryptedText= new String(plainText);
        }
        catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e)
        {
            throw new CipherException(e.getMessage(),e);
        }

        return decryptedText;
    }
}
