package model.credentialsReaderWriter;

import java.io.*;
import java.util.Scanner;

public class CredentialsReader
{
    Credentials credentials = new Credentials();
    private String credFilePath;

    public Credentials GetCredentials(String filePath)
    {
        credFilePath = filePath;
        try ( BufferedReader reader = new BufferedReader(new FileReader(credFilePath)))
        {
            String line = reader.readLine();
            if( !IsLineInInValidFormat(line) )
            {
                System.err.println("Credentials file format is invalid.");
                ReadCredentialsFromStdinAndSave();
            }
            else
            {
                LoadCredentials(line.split(","));
            }
        }
        catch (IOException e) {
            System.err.println("Failed to open file with credentials");
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

    private void LoadCredentials(String[] credentialsArray)
    {
        TrippleDes td = new TrippleDes();
        credentials.username    = credentialsArray[0];
        credentials.password    = td.decrypt(credentialsArray[1]);
        credentials.hostname    = credentialsArray[2];
        credentials.databaseName = credentialsArray[3];
        credentials.dbURL        = "jdbc:sqlserver://" + credentials.hostname + ";" + "databaseName="+credentials.databaseName;
    }
}
