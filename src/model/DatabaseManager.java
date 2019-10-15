package model;

import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Scanner;

public class DatabaseManager
{
    enum state
    {
        SUCCESS,
        FAILURE
    };
    private String username;
    private String pass;
    private String hostname;
    private String databaseName;
    private String credFilePath;

    public DatabaseManager(String filePath)
    {
        /*try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/
        credFilePath =filePath;
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
    }

    void SaveCredentials()
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

    String getProductName(String barcode)
    {

        return null;
    }
    state GetCredentialsFromFile() {
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

    public HashMap<String,Double> GetWarehouseData()
    {
        HashMap<String,Double> barcodeToQuantity = new HashMap<>();
        String dbURL = "jdbc:sqlserver://" + hostname + ";" + "databaseName="+databaseName;
        Connection conn = null;
        try
        {
            conn = DriverManager.getConnection(dbURL, username, pass);
            if (conn != null)
            {
                Statement st = conn.createStatement();
                ResultSet res = st.executeQuery(
                        "select sFactCode,sstRemain1 " +
                        "        FROM SSTORE " +
                        "        JOIN smast on sstore.sfileId=smast.sfileid " +
                        "        where spaFileIdNo=2;");
                while (res.next())
                {
                    double quantity = res.getDouble(2);
                    String barcode = res.getString(1);
                    barcodeToQuantity.put(barcode,quantity);
                }
                conn.commit();
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
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

            }
        }
        return  barcodeToQuantity;
    }
    state TestConnection()
    {
        String dbURL = "jdbc:sqlserver://" + hostname + ";" + "databaseName="+databaseName;
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

}
