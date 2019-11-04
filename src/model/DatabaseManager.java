package model;

import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Scanner;

public class DatabaseManager
{

    public class HashValue
    {
        double quantity;
        double lastPrcPr;
        HashValue(double q,double l)
        {
            quantity=q;
            lastPrcPr=l;
        }
    }
    private String username;
    private String pass;
    private String hostname;
    private String databaseName;
    private String credFilePath;
    private final String storageId = "2";
    private String dbURL;

    public DatabaseManager(String filePath)
    {
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

    public String getProductName(String barcode)
    {
        Connection conn = null;
        String productName = null;
        try
        {
            conn = DriverManager.getConnection(dbURL, username, pass);
            if (conn != null)
            {
                Statement st = conn.createStatement();
                ResultSet res = st.executeQuery(
                        "select sname " +
                            "FROM SMAST " +
                            "where sfactCode="+"'"+barcode+"';");
                while (res.next())
                {
                    productName = res.getString(1);

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
        return productName;
    }

    private state GetCredentialsFromFile() {
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

    public HashMap<String,HashValue> GetDataFromWarehouse()
    {
        HashMap<String,HashValue> storageHashMap = new HashMap<>();

        Connection conn = null;
        try
        {
            conn = DriverManager.getConnection(dbURL, username, pass);
            if (conn != null)
            {
                Statement st = conn.createStatement();
                ResultSet res = st.executeQuery(
                        "select sFactCode,sstRemain1,sLastPrcPr" +
                        "        FROM SSTORE " +
                        "        JOIN smast on sstore.sfileId=smast.sfileid " +
                        "        where spaFileIdNo="+storageId);
                /*SELECT SSTORE.sfileid,sname,sFactCode, sstRemain1,sLastPrcPr,stDate,stDoc,stTransKind,SpaFileIdNo
                FROM SSTORE
                JOIN SMAST on SSTORE.sfileId=SMAST.sfileid
                JOIN STRN on STRN.sFileId = SSTORE.sFileId
                where SpaFileIdNo='2' and (stTranskind=7 or stTranskind=6) and stLocation=2
                order by stDate desc;*/


                while (res.next())
                {
                    double lastPrcPr    = res.getDouble(3);
                    double quantity     = res.getDouble(2);
                    String barcode      = res.getString(1);
                    HashValue val = new HashValue(quantity,lastPrcPr);
                    storageHashMap.put(barcode,val);
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
        return  storageHashMap;
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


}
