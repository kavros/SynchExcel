package model.credentialsReaderWriter;

import javassist.bytecode.stackmap.TypeData;
import java.io.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CredentialsReader
{
    Credentials credentials = new Credentials();
    private String credFilePath;
    private static final Logger logger = Logger.getLogger( TypeData.ClassName.class.getName() );

    public Credentials GetCredentials(String filePath)
    {
        credFilePath = filePath;
        try ( BufferedReader reader = new BufferedReader(new FileReader(credFilePath)))
        {
            String line = reader.readLine();
            if( !IsLineInInValidFormat(line) )
            {
                logger.log(Level.WARNING,"Credentials file format is invalid.");
                ReadCredentialsFromStdinAndSave();
            }
            else
            {
                LoadCredentials(line.split(","));
            }
        }
        catch (IOException e) {
            logger.log(Level.WARNING,"Failed to open file with credentials");
            ReadCredentialsFromStdinAndSave();
        }
        catch (EncrypterDecrypterException e)
        {
            logger.log(Level.WARNING,"Decryption failed.");
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
        CredentialsWriter.SaveCredentials(credentials,credFilePath);
    }

    private void GetCredentialsFromUser()
    {
        Scanner scan = new Scanner(System.in);

        System.out.print("Username:");
        credentials.username = scan.next();
        System.out.print("Password:");
        credentials.password = scan.next();
        System.out.print("Hostname:");
        credentials.hostname = scan.next();
        System.out.print("Database name:");
        credentials.databaseName = scan.next();
        credentials.dbURL = "jdbc:sqlserver://" + credentials.hostname + ";" + "databaseName="+credentials.databaseName;
    }

    private void LoadCredentials(String[] credentialsArray) throws EncrypterDecrypterException
    {
        EncrypterDecrypter td = new EncrypterDecrypter();
        credentials.username = credentialsArray[0];
        credentials.password = td.decrypt(credentialsArray[1]);
        credentials.hostname = credentialsArray[2];
        credentials.databaseName = credentialsArray[3];
        credentials.dbURL = "jdbc:sqlserver://" + credentials.hostname + ";" + "databaseName=" + credentials.databaseName;
    }
}
