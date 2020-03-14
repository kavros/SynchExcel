package model.logins;

import javassist.bytecode.stackmap.TypeData;
import model.cipher.Cipher;
import model.cipher.CipherException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CredentialsWriter
{
    Cipher cipher ;
    public CredentialsWriter(Cipher cipher)
    {
        this.cipher  = cipher ;
    }

    private static final Logger logger = Logger.getLogger( TypeData.ClassName.class.getName() );

    public void SaveCredentials(Credentials credentials, String credFilePath)
    {
        if(credFilePath == null || credentials == null)
            throw new IllegalArgumentException("File path is not given");

        try( BufferedWriter writer = new BufferedWriter(new FileWriter(credFilePath)) )
        {
            String encrypted = cipher.encrypt(credentials.password);
            String credentialsSaveFormat = credentials.username+","+encrypted+","+credentials.hostname+","+credentials.databaseName;
            writer.write(credentialsSaveFormat);
            writer.close();
        }
        catch(IOException | CipherException e)
        {
            logger.log(Level.SEVERE,"Error: Failed to save file \n"+e);
        }
    }
}
