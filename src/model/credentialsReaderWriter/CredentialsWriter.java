package model.credentialsReaderWriter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CredentialsWriter
{

    public static void SaveCredentials(Credentials credentials, String credFilePath)
    {
        if(credFilePath == null || credentials == null)
            throw new IllegalArgumentException("File path is not given");

        TrippleDes td = new TrippleDes();
        String encrypted = td.encrypt(credentials.password);
        String credentialsSaveFormat = credentials.username+","+encrypted+","+credentials.hostname+","+credentials.databaseName;
        try( BufferedWriter writer = new BufferedWriter(new FileWriter(credFilePath)) )
        {
            writer.write(credentialsSaveFormat);
            writer.close();
        }catch(IOException e)
        {
            System.err.println("Error: Failed to save file");
        }
    }
}
