package model;

import java.io.*;
import java.util.Scanner;

public class CredentialsManager
{

    private String username;
    private String pass;
    private String hostname;
    private String dbURL;
    private String credFilePath;
    private String databaseName;

    public void GetUserInputs()
    {
        Scanner scan = new Scanner(System.in);
        Console console = System.console();

        System.out.print("Username:");
        username = scan.next();
        System.out.print("Password:");
        pass = scan.next();
        //pass = new String(console.readPassword("Password: "));
        System.out.print("Hostname:");
        hostname = scan.next();
        System.out.print("Database name:");
        databaseName = scan.next();
        dbURL = "jdbc:sqlserver://" + hostname + ";" + "databaseName="+databaseName;
    }

    public void SaveCredentials()
    {
        try
        {
            TrippleDes td = new TrippleDes();
            String encrypted = td.encrypt(pass);
            String decrypted = td.decrypt(encrypted);

            String credentials=username+","+encrypted+","+hostname+","+databaseName;
            BufferedWriter writer = new BufferedWriter(new FileWriter(credFilePath));
            writer.write(credentials);
            writer.close();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }

    }

    public state GetCredentialsFromFile(String filePath)
    {
        credFilePath = filePath;
        try
        {
            File file = new File(credFilePath);
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line = br.readLine();
            if(line==null || line.isEmpty())
            {
                return state.FAILURE;
            }

            String[] details = line.split(",");
            TrippleDes td = new TrippleDes();
            username    = details[0];
            pass        = td.decrypt(details[1]);
            hostname    = details[2];
            databaseName = details[3];
            dbURL        = "jdbc:sqlserver://" + hostname + ";" + "databaseName="+databaseName;
            if(username==null || pass==null|| hostname==null)
            {
                return state.FAILURE;
            }

        }
        catch (Exception e)
        {
            System.out.println(e);
            return state.FAILURE;
        }

        return  state.SUCCESS;
    }

    public String GetUsername()
    {
        return username;
    }

    public String GetPass()
    {
        return pass;
    }

    public String GetHostname()
    {
        return hostname;
    }

    public String GetDbURL()
    {
        return dbURL;
    }

    public String GetDatabaseName()
    {
        return databaseName;
    }

}
