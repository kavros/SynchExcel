package model;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class CredentialsManager
{

    private String username;
    private String pass;
    private String hostname;
    private String dbURL;
    private String credFilePath;
    private String databaseName;

    CredentialsManager(String filePath)
    {
        credFilePath = filePath;
        if(GetCredentialsFromFile() == state.FAILURE)
        {
            RetrieveAndStoreCredentials();
        }
        else
        {
            if(TestConnection() == state.SUCCESS)
            {
                System.out.println("Database connectivity test completed.");
            }
            else
            {
                System.out.println("Database connection failed. Please enter credentials again.");
                RetrieveAndStoreCredentials();
            }
        }
    }

    private void RetrieveAndStoreCredentials()
    {
        AskUserForCred();
        while (TestConnection() == state.FAILURE)
        {
            System.out.println("Credentials are incorrect");
            AskUserForCred();
        }
        System.out.println("Database connectivity test completed.");
        SaveCredentials();

    }

    private void AskUserForCred()
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

    private void SaveCredentials()
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

    private state GetCredentialsFromFile()
    {
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
        }

        return  state.SUCCESS;
    }

    private state TestConnection()
    {
        Connection conn = null;
        try
        {
            conn = DriverManager.getConnection(dbURL, username, pass);

            if(conn == null)
            {
                System.out.println("conn is null");
                return state.FAILURE;
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
            return  state.FAILURE;
        }
        finally
        {
            try
            {
                if (conn != null && !conn.isClosed())
                {
                    conn.close();
                }
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
                return state.FAILURE;
            }
        }
        return state.SUCCESS;
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
