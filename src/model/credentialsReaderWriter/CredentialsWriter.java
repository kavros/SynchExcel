package model.credentialsReaderWriter;

import javassist.bytecode.stackmap.TypeData;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CredentialsWriter
{

    private CredentialsWriter(){}
    private static final Logger logger = Logger.getLogger( TypeData.ClassName.class.getName() );

    public static void SaveCredentials(Credentials credentials, String credFilePath)
    {
        if(credFilePath == null || credentials == null)
            throw new IllegalArgumentException("File path is not given");

        try( BufferedWriter writer = new BufferedWriter(new FileWriter(credFilePath)) )
        {
            EncrypterDecrypter td = new EncrypterDecrypter();
            String encrypted = td.encrypt(credentials.password);
            String credentialsSaveFormat = credentials.username+","+encrypted+","+credentials.hostname+","+credentials.databaseName;
            writer.write(credentialsSaveFormat);
            writer.close();
        }
        catch(IOException | EncrypterDecrypterException e)
        {
            logger.log(Level.SEVERE,"Error: Failed to save file \n"+e);
        }
    }
}
