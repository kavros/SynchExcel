package model.database.credentials;

import model.cipher.Cipher;
import model.cipher.CipherException;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.Scanner;

public class CredentialsReader
{
    Credentials credentials = new Credentials();
    private String credFilePath;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CredentialsReader.class);
    private CredentialsWriter credentialsWriter;
    private Cipher encrypterDecrypter;

    public CredentialsReader(CredentialsWriter cWriter,Cipher ed)
    {
        credentialsWriter=cWriter;
        encrypterDecrypter = ed;
    }

    public Credentials GetCredentials(String filePath)
    {
        credFilePath = filePath;
        try ( BufferedReader reader = new BufferedReader(new FileReader(credFilePath)))
        {
            String line = reader.readLine();
            if( !IsLineInInValidFormat(line) )
            {
                logger.warn("Credentials file format is invalid.");
                ReadCredentialsFromStdinAndSave();
            }
            else
            {
                LoadCredentials(line.split(","));
            }
        }
        catch (IOException e) {
            logger.warn("Failed to open file with credentials");
            ReadCredentialsFromStdinAndSave();
        }
        catch (CipherException e)
        {
            logger.warn("Decryption failed.");
            ReadCredentialsFromStdinAndSave();
        }

        return credentials;
    }


    private boolean IsLineInInValidFormat(String credentialsLine)
    {
        if( credentialsLine == null )
            return  false;

        String[] data =  credentialsLine.split(",");
        if( data.length != 4)
            return false;

        return  data[0] != null  &&
                data[1] != null  &&
                data[2] != null  &&
                data[3] != null;
    }

    private void ReadCredentialsFromStdinAndSave()
    {
        GetCredentialsFromUser();
        credentialsWriter.SaveCredentials(credentials,credFilePath);
    }

    private void GetCredentialsFromUser()
    {
        Scanner scan = new Scanner(System.in);
        logger.info("Please enter user credentials");
        logger.info("Username:");
        credentials.username = scan.next();
        logger.info("Password:");
        credentials.password = scan.next();
        logger.info("Hostname:");
        credentials.hostname = scan.next();
        logger.info("Database name:");
        credentials.databaseName = scan.next();
        credentials.dbURL = "jdbc:sqlserver://" + credentials.hostname + ";" + "databaseName="+credentials.databaseName;
    }

    private void LoadCredentials(String[] credentialsArray) throws CipherException
    {
        credentials.username = credentialsArray[0];
        credentials.password = encrypterDecrypter.decrypt(credentialsArray[1]);
        credentials.hostname = credentialsArray[2];
        credentials.databaseName = credentialsArray[3];
        credentials.dbURL = "jdbc:sqlserver://" + credentials.hostname + ";" + "databaseName=" + credentials.databaseName;
    }
}
